package dev.slne.discord.discord.interaction.modal.step;

import dev.slne.discord.discord.interaction.modal.step.ModalStep.ModalStepInputVerificationException;
import dev.slne.discord.discord.interaction.modal.step.ModalStep.ModuleStepChannelCreationException;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketType;
import dev.slne.discord.ticket.result.TicketCreateResult;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.Modal.Builder;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nullable;

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

  protected DiscordStepChannelCreationModal(String id, @Nonnull String title,
      TicketType ticketType) {
    this.id = id;
    this.title = title;
    this.ticketType = ticketType;
  }

  protected abstract StepBuilder buildSteps();

  private ModalComponentBuilder buildModalComponents() {
    final ModalComponentBuilder builder = new ModalComponentBuilder();
    getSteps().forEach(step -> step.fillModalComponents(builder));

    return builder;
  }

  public final CompletableFuture<Void> startChannelCreation(StringSelectInteraction interaction) {
    final CompletableFuture<Void> done = new CompletableFuture<>();

    interaction.deferReply(true).queue(hook -> {
      doSelectionSteps(hook).thenAcceptAsync(lastSelectionEvent -> {
        final IModalCallback callback =
            lastSelectionEvent != null ? lastSelectionEvent : interaction;
        final Modal modal = buildModal();

        // TODO: 18.08.2024 20:22 - fix

        callback.replyModal(modal)
            .queue(unused -> done.complete(null), done::completeExceptionally);
      });
    }, throwable -> {
      LOGGER.error("Failed to start channel creation", throwable);
      done.completeExceptionally(throwable);
    });

    return done;
  }

  public void submitModal(ModalInteractionEvent event) {
    final LinkedList<ModalStep> modalSteps = getSteps();
    final InteractionHook hook = event.getHook();
    final User user = event.getUser();

    verifyModalInput(event, hook, modalSteps);
    prepareChannelCreation(hook, modalSteps);

    final Ticket ticket = new Ticket(event.getGuild(), user, ticketType);
    ticket.openFromButton()
        .thenAcceptAsync(result -> afterChannelCreated(ticket, result, hook, user))
        .exceptionally(e -> {
          hook.editOriginal("Es ist ein Fehler aufgetreten!").queue();
          return null;
        });
  }

  private Modal buildModal() {
    final Builder builder = Modal.create(title, title);

    for (final ActionComponent component : buildModalComponents().getComponents()) {
      builder.addActionRow(component);
    }

    return builder.build();
  }

  private CompletableFuture<@Nullable StringSelectInteractionEvent> doSelectionSteps(
      InteractionHook hook) {
    return CompletableFuture.supplyAsync(() -> doSelectionSteps(hook, getSteps(), null, null));
  }

//  private @Nullable StringSelectInteractionEvent doSelectionSteps(InteractionHook hook,
//      LinkedList<ModalStep> steps) {
//
//
//  }

  private @Nullable StringSelectInteractionEvent doSelectionSteps(InteractionHook hook,
      LinkedList<ModalStep> steps, StringSelectInteractionEvent lastEvent, Message lastMessage) {

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
        doSelectionSteps(hook, children, lastEvent, lastMessage);
      }
    }

    return lastEvent;
  }

  private void verifyModalInput(ModalInteractionEvent event, InteractionHook hook,
      LinkedList<ModalStep> steps) {
    for (final ModalStep step : steps) {
      try {
        step.runVerifyModalInput(event);
      } catch (ModalStepInputVerificationException e) {
        hook.editOriginal(e.getMessage()).queue();
        return;
      }
    }
  }

  private void prepareChannelCreation(InteractionHook hook, LinkedList<ModalStep> steps) {
    for (final ModalStep step : steps) {
      try {
        step.runPrepareChannelCreationAsync();
      } catch (ModuleStepChannelCreationException e) {
        hook.editOriginal(e.getMessage()).queue();
        return;
      }
    }
  }

  private void afterChannelCreated(Ticket ticket, TicketCreateResult result, InteractionHook hook,
      User user) {
    final TextChannel channel = ticket.getChannel();

    switch (result) {
      case SUCCESS -> handleSuccess(channel, hook, user);
      case ALREADY_EXISTS -> editHook(hook,
          "Du hast bereits ein Ticket mit dem angegeben Typ geöffnet. Sollte dies nicht der Fall sein, wende dich per Ping an @notammo.");
      case MISSING_PERMISSIONS -> editHook(hook,
          "Du hast nicht die benötigten Berechtigungen, um ein Ticket zu erstellen!");
      default -> {
        editHook(hook, "Es ist ein Fehler aufgetreten (qopuewuopfbop8729)!");
        LOGGER.error("Ticket creation failed with result: {}", result);
      }
    }
  }

  private void editHook(InteractionHook hook, String message) {
    hook.editOriginal(message).queue();
  }

  private void handleSuccess(TextChannel channel, InteractionHook hook, User user) {
    final StringBuilder message = new StringBuilder();
    message.append("Dein \"");
    message.append(ticketType.getName());
    message.append("\"-Ticket wurde erfolgreich erstellt! ");

    if (channel != null) {
      message.append(channel.getAsMention());
    }

    hook.editOriginal(message.toString()).queue();

    if (channel != null) {
      doWithCreatedChannel(channel, user);
    }
  }

  private void doWithCreatedChannel(TextChannel channel, User user) {
    final MessageQueue messages = new MessageQueue();

    getOpenMessages(messages, channel, user);
    getSteps().forEach(step -> step.getOpenMessages(messages, channel));

    final LinkedList<String> message = messages.buildMessages();

    if (!message.isEmpty()) {
      sendOpenMessage(message, channel);
    }

    runAfterChannelCreated(channel);
    getSteps().forEach(step -> step.runAfterChannelCreated(channel));
  }

  private void sendOpenMessage(LinkedList<String> messages, TextChannel channel) {
    if (messages.isEmpty()) {
      return;
    }

    channel.sendMessage(messages.getFirst()).queue(message -> { // ensure messages are sent in order
      messages.removeFirst();
      sendOpenMessage(messages, channel);
    });
  }

  protected void getOpenMessages(MessageQueue messages, TextChannel channel, User user) {

  }

  protected void runAfterChannelCreated(TextChannel channel) {

  }
}
