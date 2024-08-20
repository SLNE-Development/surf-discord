package dev.slne.discord.discord.interaction.modal.step;

import java.io.Serial;
import java.util.LinkedList;
import lombok.Getter;
import lombok.experimental.StandardException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a step in a modal process, which may include input validation, preparation, and
 * finalization.
 */
public abstract class ModalStep {

  @Getter(lazy = true)
  private final LinkedList<ModalStep> children = buildChildSteps().getSteps();

  /**
   * Builds the components for this modal step.
   *
   * @param builder The builder used to construct the modal components.
   */
  @OverrideOnly
  protected abstract void buildModalComponents(ModalComponentBuilder builder);

  /**
   * Fills the provided builder with modal components for this step and its children.
   *
   * @param builder The builder to be filled with components.
   */
  public final void fillModalComponents(ModalComponentBuilder builder) {
    buildModalComponents(builder);
    getChildren().forEach(child -> child.fillModalComponents(builder));
  }

  /**
   * Verifies the input provided in the modal interaction event for this step.
   *
   * <p>This method should be overridden by subclasses to implement specific input validation
   * logic.</p>
   *
   * @param event The modal interaction event containing user input.
   * @throws ModalStepInputVerificationException if the input is invalid.
   */
  @Blocking
  @OverrideOnly
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    // Override to implement input validation
  }

  /**
   * Runs the input verification for this step and all of its children.
   *
   * @param event The modal interaction event containing user input.
   * @throws ModalStepInputVerificationException if any input is invalid.
   */
  public final void runVerifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    verifyModalInput(event);
    for (final ModalStep child : getChildren()) {
      child.runVerifyModalInput(event);
    }
  }

  /**
   * Retrieves the required input from the modal interaction event by its key.
   *
   * @param event The modal interaction event.
   * @param key   The key identifying the input field.
   * @return The input value as a string.
   * @throws ModalStepInputVerificationException if the required input is missing.
   */
  protected final @NotNull String getRequiredInput(@NotNull ModalInteractionEvent event, String key)
      throws ModalStepInputVerificationException {
    final ModalMapping value = event.getValue(key);

    if (value == null) {
      throw new ModalStepInputVerificationException("Missing required input: " + key);
    }

    return value.getAsString();
  }

  /**
   * Retrieves optional input from the modal interaction event by its key.
   *
   * @param event The modal interaction event.
   * @param key   The key identifying the input field.
   * @return The input value as a string, or null if not provided.
   */
  protected final @Nullable String getOptionalInput(
      @NotNull ModalInteractionEvent event,
      String key
  ) {
    final ModalMapping value = event.getValue(key);

    if (value == null) {
      return null;
    }

    return value.getAsString();
  }

  /**
   * Prepares the channel creation process asynchronously.
   *
   * <p>This method should be overridden by subclasses to implement specific preparation logic.</p>
   *
   * @throws ModuleStepChannelCreationException if an error occurs during preparation.
   */
  @SuppressWarnings("RedundantThrows")
  @Blocking
  @OverrideOnly
  protected void prepareChannelCreationAsync() throws ModuleStepChannelCreationException {
    // Override to implement preparation logic
  }

  /**
   * Runs the preparation logic for channel creation for this step and all of its children.
   *
   * @throws ModuleStepChannelCreationException if an error occurs during preparation.
   */
  public final void runPreChannelCreationAsync() throws ModuleStepChannelCreationException {
    prepareChannelCreationAsync();
    for (final ModalStep child : getChildren()) {
      child.runPreChannelCreationAsync();
    }
  }

  /**
   * Builds the messages that should be displayed when the channel is opened.
   *
   * <p>This method should be overridden by subclasses to add custom messages.</p>
   *
   * @param messages The message queue to which messages are added.
   * @param channel  The channel where the messages will be sent.
   */
  @OverrideOnly
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    // Override to add custom open messages
  }

  /**
   * Retrieves the open messages for this step and all of its children.
   *
   * @param messages The message queue to which messages are added.
   * @param channel The channel where the messages will be sent.
   */
  public final void getOpenMessages(MessageQueue messages, TextChannel channel) {
    buildOpenMessages(messages, channel);
    for (ModalStep child : getChildren()) {
      child.getOpenMessages(messages, channel);
    }
  }

  /**
   * Executes custom logic after the channel has been created.
   *
   * <p>This method should be overridden by subclasses to add post-creation logic.</p>
   *
   * @param channel The channel that was created.
   */
  @OverrideOnly
  protected void onPostChannelCreated(@SuppressWarnings("unused") TextChannel channel) {
  }

  /**
   * Runs the post-creation logic for this step and all of its children.
   *
   * @param channel The channel that was created.
   */
  public final void runPostChannelCreated(TextChannel channel) {
    onPostChannelCreated(channel);
    for (ModalStep child : getChildren()) {
      child.runPostChannelCreated(channel);
    }
  }

  /**
   * Builds the child steps for this modal step.
   *
   * @return A StepBuilder containing the child steps.
   */
  @OverrideOnly
  public StepBuilder buildChildSteps() {
    return StepBuilder.empty();
  }

  /**
   * Checks if this step or any of its children involve a selection process.
   *
   * @return true if this step or any child has a selection process, false otherwise.
   */
  public final boolean hasSelectionStep() {
    if (this instanceof ModalSelectionStep) {
      return true;
    }

    for (final ModalStep child : getChildren()) {
      if (child.hasSelectionStep()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Exception thrown when a modal step's input verification fails.
   */
  @StandardException
  public static class ModalStepInputVerificationException extends Exception {

    @Serial
    private static final long serialVersionUID = 7427250171852391L;
  }

  /**
   * Exception thrown when a channel creation step fails.
   */
  @StandardException
  public static class ModuleStepChannelCreationException extends
      ModalStepInputVerificationException {

    @Serial
    private static final long serialVersionUID = -4690438162011591307L;
  }
}
