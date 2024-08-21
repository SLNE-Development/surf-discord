package dev.slne.discord.discord.interaction.modal.step;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.modal.step.ModalStep.ModalStepInputVerificationException;
import dev.slne.discord.discord.interaction.modal.step.ModalStep.ModuleStepChannelCreationException;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannelUtil;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.Modal.Builder;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a modal for creating a channel step by step in Discord.
 *
 * <p>This abstract class is intended to be extended to define the specific steps required for
 * creating a channel via modals in Discord. It handles the initialization and management of these
 * steps, as well as the interaction flow for users.</p>
 */
@Getter
@Accessors(makeFinal = true)
public abstract class DiscordStepChannelCreationModal {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(
      "DiscordStepChannelCreationModal");

  private final String id;
  @Nonnull
  private final String title;
  private final TicketType ticketType;

  @Getter(lazy = true)
  private final LinkedList<ModalStep> steps = buildSteps().getSteps();

  /**
   * Constructs a new DiscordStepChannelCreationModal with the specified parameters.
   *
   * @param id         The unique identifier for this modal.
   * @param title      The title of the modal.
   * @param ticketType The type of ticket associated with this modal.
   */
  protected DiscordStepChannelCreationModal(String id, @Nonnull String title,
      TicketType ticketType) {
    this.id = id;
    this.title = title;
    this.ticketType = ticketType;
  }

  /**
   * Builds the steps that will be used in this modal.
   *
   * @return A StepBuilder object containing the steps.
   * @see StepBuilder#startWith(ModalStep)
   */
  @OverrideOnly
  protected abstract StepBuilder buildSteps();

  private @NotNull ModalComponentBuilder buildModalComponents() {
    final ModalComponentBuilder builder = new ModalComponentBuilder();
    getSteps().forEach(step -> step.fillModalComponents(builder));

    return builder;
  }

  /**
   * Initiates the channel creation process starting with any selection steps if required.
   *
   * @param interaction The interaction triggering the channel creation.
   * @return A CompletableFuture that will be completed when the channel creation process is done.
   */
  public final CompletableFuture<Void> startChannelCreation(
      @NotNull StringSelectInteraction interaction) {
    final CompletableFuture<Void> done = new CompletableFuture<Void>()
        .exceptionally(throwable -> {
          LOGGER.error("Error while creating ticket", throwable);

          interaction.deferReply(true)
              .queue(hook -> {
                hook.sendMessage("Es ist ein Fehler aufgetreten! (gadgdgh68797997)").queue();
              });
          return null;
        });

    if (checkTicketExists(interaction.getGuild(), interaction.getUser())) {
      reply(interaction,
          "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.");
      done.complete(null);
      return done;
    }

    DiscordBot.getInstance().getModalManager()
        .setCurrentUserModal(interaction.getUser().getId(), this);

    if (!hasSelectionStep()) {
      replyModal(interaction, done);
      return done;
    }

    startChannelCreationWithSelectionSteps(interaction, done);

    return done;
  }

  /**
   * Starts the channel creation process, handling any required selection steps.
   *
   * @param interaction The interaction triggering the channel creation.
   * @param done        A CompletableFuture to signal the completion of the process.
   */
  private void startChannelCreationWithSelectionSteps(@NotNull StringSelectInteraction interaction,
      CompletableFuture<Void> done) {
    interaction.deferReply(true)
        .queue(hook -> executeSelectionSteps(hook)
            .thenAcceptAsync(
                lastSelectionEvent -> replyModalAfterSelectionSteps(lastSelectionEvent, interaction,
                    done)), throwable -> {
          LOGGER.error("Failed to start channel creation", throwable);
          done.completeExceptionally(throwable);
        });
  }

  /**
   * Replies with the modal after handling the selection steps.
   *
   * @param lastSelectionEvent The last selection event that occurred or null if there was none.
   * @param interaction        The original interaction that started the process.
   * @param done               A CompletableFuture to signal the completion of the process.
   */
  private void replyModalAfterSelectionSteps(
      @Nullable StringSelectInteractionEvent lastSelectionEvent,
      StringSelectInteraction interaction,
      CompletableFuture<Void> done
  ) {
    final IModalCallback callback =
        lastSelectionEvent != null ? lastSelectionEvent : interaction;

    if (lastSelectionEvent != null) {
      lastSelectionEvent.getMessage().delete().queue();
    }

    replyModal(callback, done);
  }

  /**
   * Replies to the interaction with the modal.
   *
   * @param modalCallback The callback to reply to.
   * @param callback      A CompletableFuture to signal the completion of the process.
   */
  private void replyModal(@NotNull IModalCallback modalCallback,
      @NotNull CompletableFuture<Void> callback) {
    modalCallback.replyModal(buildModal())
        .queue(unused -> callback.complete(null), callback::completeExceptionally);
  }

  /**
   * Submits the modal input and proceeds with channel creation.
   *
   * @param event The modal interaction event.
   */
  public final void handleUserSubmitModal(@NotNull ModalInteractionEvent event) {
    final LinkedList<ModalStep> modalSteps = getSteps();
    final User user = event.getUser();

    verifyModalInput(event, modalSteps);
    preChannelCreation(event, modalSteps);

    final Ticket ticket = new Ticket(event.getGuild(), user, ticketType);
    ticket.openFromButton()
        .thenAcceptAsync(result -> postChannelCreated(ticket, result, event, user))
        .exceptionally(e -> {
          event.deferReply(true).queue(
              hook -> hook.sendMessage("Es ist ein Fehler aufgetreten! (ophdo9upou76967867)")
                  .queue());
          LOGGER.error("Error while creating ticket", e);
          return null;
        });
  }

  private boolean checkTicketExists(Guild guild, User user) {
    return TicketChannelUtil.checkTicketExistsFast(guild, ticketType, user);
  }

  /**
   * Builds the modal from the components generated by the steps.
   *
   * @return The built Modal object.
   */
  private @NotNull Modal buildModal() {
    final Builder builder = Modal.create(id, title);

    for (final ActionComponent component : buildModalComponents().getComponents()) {
      builder.addActionRow(component);
    }

    return builder.build();
  }

  /**
   * Performs the selection steps sequentially, waiting for user interaction at each step.
   *
   * @param hook The interaction hook used to send messages and interact with the user.
   * @return A CompletableFuture that will complete with the last StringSelectInteractionEvent, or
   * null if none occurred.
   */
  @Contract("_ -> new")
  private @NotNull CompletableFuture<@Nullable StringSelectInteractionEvent> executeSelectionSteps(
      InteractionHook hook
  ) {
    return CompletableFuture.supplyAsync(() -> executeSelectionSteps(hook, getSteps(), null, null));
  }

  /**
   * Performs the selection steps recursively.
   *
   * @param hook        The interaction hook used to send messages and interact with the user.
   * @param steps       The list of steps to process.
   * @param lastEvent   The last StringSelectInteractionEvent, which occurred, or null if none.
   * @param lastMessage The last Message that was sent or null if none.
   * @return The last StringSelectInteractionEvent, which occurred, or null if none.
   */
  private @Nullable StringSelectInteractionEvent executeSelectionSteps(
      InteractionHook hook,
      @NotNull LinkedList<ModalStep> steps,
      StringSelectInteractionEvent lastEvent,
      Message lastMessage
  ) {

    for (final ModalStep step : steps) {
      if (step instanceof ModalSelectionStep selectionStep) {
        if (lastMessage != null) {
          lastMessage.delete().queue();
        }

        AtomicReference<Message> message = new AtomicReference<>();

        hook.sendMessage(selectionStep.getSelectTitle())
            .setEphemeral(true)
            .setActionRow(selectionStep.createSelection()).queue(message::set,
                Throwable::printStackTrace);

        lastEvent = selectionStep.getSelectionFuture().join();
        lastMessage = message.get();
      }

      final LinkedList<ModalStep> children = step.getChildren();
      if (!children.isEmpty()) {
        lastEvent = executeSelectionSteps(hook, children, lastEvent, lastMessage);
      }
    }

    return lastEvent;
  }

  /**
   * Verifies the user input for all steps.
   *
   * @param event The modal interaction event.
   * @param steps The list of steps to verify.
   */
  private void verifyModalInput(ModalInteractionEvent event, @NotNull LinkedList<ModalStep> steps) {
    for (final ModalStep step : steps) {
      try {
        step.runVerifyModalInput(event);
      } catch (ModalStepInputVerificationException e) {
        event.reply(e.getMessage()).setEphemeral(true).queue();
        return;
      }
    }
  }

  /**
   * Prepares the channel creation process for all steps.
   *
   * @param event The modal interaction event.
   * @param steps The list of steps to process.
   */
  private void preChannelCreation(
      ModalInteractionEvent event,
      @NotNull LinkedList<ModalStep> steps
  ) {
    for (final ModalStep step : steps) {
      try {
        step.runPreChannelCreationAsync();
      } catch (ModuleStepChannelCreationException e) {
        event.reply(e.getMessage()).setEphemeral(true).queue();
        return;
      }
    }
  }

  /**
   * Handles the result of the channel creation process.
   *
   * @param ticket The ticket that was created.
   * @param result The result of the ticket creation.
   * @param event  The modal interaction event.
   * @param user   The user who initiated the channel creation.
   */
  private void postChannelCreated(
      @NotNull Ticket ticket,
      @NotNull TicketCreateResult result,
      ModalInteractionEvent event,
      User user
  ) {
    final TextChannel channel = ticket.getChannel();

    switch (result) {
      case SUCCESS -> handleSuccess(channel, event, user);
      case ALREADY_EXISTS -> reply(event,
          "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.");
      case MISSING_PERMISSIONS -> reply(event,
          "Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!");
      default -> {
        reply(event, "Es ist ein Fehler aufgetreten (qopuewuopfbop8729)!");
        LOGGER.error("Ticket creation failed with result: {}", result);
      }
    }
  }

  /**
   * Replies to the user with a specified message.
   *
   * @param callback The callback to reply to.
   * @param message  The message to send.
   */
  private void reply(@NotNull IReplyCallback callback, String message) {
    if (callback.isAcknowledged()) {
      callback.getHook().sendMessage(message).setEphemeral(true).queue();
    } else {
      callback.deferReply(true).queue(hook -> hook.sendMessage(message).queue());
    }
  }

  /**
   * Handles successful channel creation, sending a confirmation message to the user and performing
   * any additional setup.
   *
   * @param channel The channel that was created.
   * @param event   The modal interaction event.
   * @param user    The user who initiated the channel creation.
   */
  private void handleSuccess(TextChannel channel, ModalInteractionEvent event, User user) {
    final StringBuilder message = new StringBuilder();
    message.append("Dein \"");
    message.append(ticketType.getName());
    message.append("\"-Ticket wurde erfolgreich erstellt! ");

    if (channel != null) {
      message.append(channel.getAsMention());
    }

    reply(event, message.toString());

    if (channel != null) {
      doWithCreatedChannel(channel, user);
    }
  }

  /**
   * Performs actions on the newly created channel, such as sending initial messages.
   *
   * @param channel The channel that was created.
   * @param user    The user who initiated the channel creation.
   */
  private void doWithCreatedChannel(TextChannel channel, User user) {
    final MessageQueue messages = new MessageQueue();

    getOpenMessages(messages, channel, user);
    for (final ModalStep step : getSteps()) {
      step.getOpenMessages(messages, channel);
    }

    final LinkedList<String> message = messages.buildMessages();

    sendOpenMessage(message, channel);

    onPostChannelCreated(channel);
    getSteps().forEach(step -> step.runPostChannelCreated(channel));
  }

  /**
   * Sends the queued messages to the specified channel in order.
   *
   * @param messages The list of messages to send.
   * @param channel  The channel to send the messages to.
   */
  private void sendOpenMessage(LinkedList<String> messages, TextChannel channel) {
    if (messages.isEmpty()) {
      return;
    }

    channel.sendMessage(messages.getFirst()).queue(message -> { // ensure messages are sent in order
      messages.removeFirst();
      sendOpenMessage(messages, channel);
    });
  }

  /**
   * Provides open messages related to the created channel.
   *
   * @param messages The message queue to which open messages are added.
   * @param channel  The channel where the messages will be sent.
   * @param user     The user associated with the channel creation.
   */
  @OverrideOnly
  protected void getOpenMessages(MessageQueue messages, TextChannel channel, User user) {
    // Override if necessary
  }

  /**
   * Executes custom logic after the channel has been created.
   *
   * @param channel The channel that was created.
   */
  @OverrideOnly
  protected void onPostChannelCreated(TextChannel channel) {
    // Override if necessary
  }

  /**
   * Determines if any of the steps involve a selection process.
   *
   * @return true if any step has a selection process, false otherwise.
   */
  public final boolean hasSelectionStep() {
    return getSteps().stream().anyMatch(ModalStep::hasSelectionStep);
  }
}
