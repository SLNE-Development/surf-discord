package dev.slne.discord.discord.interaction.modal.step;

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedList;
import java.util.function.Function;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A builder class for creating a sequence of modal steps.
 */
public final class StepBuilder {

  @Getter
  private final LinkedList<ModalStep> steps = new LinkedList<>();
  private ModalStep lastStep;

  private StepBuilder(ModalStep firstStep) {
    lastStep = firstStep;
    steps.add(lastStep);
  }

  private StepBuilder() {
  }

  /**
   * Starts the builder with the specified first step.
   *
   * @param firstStep The first step in the sequence.
   * @return A new StepBuilder instance.
   */
  @Contract("_ -> new")
  public static @NotNull StepBuilder startWith(ModalStep firstStep) {
    return new StepBuilder(firstStep);
  }

  /**
   * Creates an empty StepBuilder.
   *
   * @return A new empty StepBuilder instance.
   */
  @Contract(" -> new")
  public static @NotNull StepBuilder empty() {
    return new StepBuilder();
  }

  /**
   * Adds a step to the sequence, using a function to generate the next step from the current one.
   *
   * @param step A function that generates the next step.
   * @return The current StepBuilder instance.
   */
  @Contract("_ -> this")
  public StepBuilder then(@NotNull Function<ModalStep, ModalStep> step) {
    checkState(lastStep != null, "Cannot add a step to an empty builder");

    lastStep = step.apply(lastStep);
    steps.add(lastStep);

    return this;
  }

  /**
   * Retrieves the first step in the sequence.
   *
   * @return The first ModalStep in the sequence.
   */
  @Contract(pure = true)
  ModalStep getFirstStep() {
    return steps.getFirst();
  }
}
