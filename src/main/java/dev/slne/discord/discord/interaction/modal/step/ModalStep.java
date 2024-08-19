package dev.slne.discord.discord.interaction.modal.step;

import java.io.Serial;
import java.util.LinkedList;
import lombok.experimental.StandardException;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

public abstract class ModalStep {

  private LinkedList<ModalStep> children;

  protected abstract void buildModalComponents(ModalComponentBuilder builder);

  public final void fillModalComponents(ModalComponentBuilder builder) {
    buildModalComponents(builder);
    getChildren().forEach(child -> child.fillModalComponents(builder));
  }

  @Blocking
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {

  }

  public final void runVerifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    verifyModalInput(event);
    for (final ModalStep child : getChildren()) {
      child.runVerifyModalInput(event);
    }
  }

  protected final String getRequiredInput(ModalInteractionEvent event, String key)
      throws ModalStepInputVerificationException {
    final ModalMapping value = event.getValue(key);

    if (value == null) {
      throw new ModalStepInputVerificationException("Missing required input: " + key);
    }

    return value.getAsString();
  }

  protected final @Nullable String getOptionalInput(ModalInteractionEvent event, String key) {
    final ModalMapping value = event.getValue(key);

    if (value == null) {
      return null;
    }

    return value.getAsString();
  }

  @Blocking
  protected void prepareChannelCreationAsync() throws ModuleStepChannelCreationException {
  }

  public final void runPrepareChannelCreationAsync() throws ModuleStepChannelCreationException {
    prepareChannelCreationAsync();
    for (final ModalStep child : getChildren()) {
      child.runPrepareChannelCreationAsync();
    }
  }

  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
  }

  public final void getOpenMessages(MessageQueue messages, TextChannel channel) {
    buildOpenMessages(messages, channel);
    for (ModalStep child : getChildren()) {
      child.getOpenMessages(messages, channel);
    }
  }

  protected void afterChannelCreated(TextChannel channel) {
  }

  public final void runAfterChannelCreated(TextChannel channel) {
    afterChannelCreated(channel);
    for (ModalStep child : getChildren()) {
      child.runAfterChannelCreated(channel);
    }
  }

  public StepBuilder buildChildSteps() {
    return StepBuilder.empty();
  }

  public final LinkedList<ModalStep> getChildren() {
    if (children == null) {
      children = buildChildSteps().getSteps();
    }

    return children;
  }

  public final boolean hasSelectionStep() {
    if (this instanceof ModalSelectionStep) {
      return true;
    }

    for (ModalStep child : getChildren()) {
      if (child.hasSelectionStep()) {
        return true;
      }
    }

    return false;
  }

  @StandardException
  public static class ModalStepInputVerificationException extends Exception {

    @Serial
    private static final long serialVersionUID = 7427250171852391L;
  }

  @StandardException
  public static class ModuleStepChannelCreationException extends
      ModalStepInputVerificationException {

    @Serial
    private static final long serialVersionUID = -4690438162011591307L;
  }
}
