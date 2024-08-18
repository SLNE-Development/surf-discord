package dev.slne.discord.discord.interaction.modal;

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedList;
import java.util.function.Function;

public class StepBuilder {

  private final LinkedList<ModalStep> steps = new LinkedList<>();
  private ModalStep lastStep;

  private StepBuilder(ModalStep firstStep) {
    lastStep = firstStep;
    steps.add(lastStep);
  }

  private StepBuilder() {

  }

  public static StepBuilder startWith(ModalStep firstStep) {
    return new StepBuilder(firstStep);
  }

  public static StepBuilder empty() {
    return new StepBuilder();
  }

  public StepBuilder then(Function<ModalStep, ModalStep> step) {
    checkState(lastStep != null, "Cannot add a step to an empty builder");

    lastStep = step.apply(lastStep);
    steps.add(lastStep);

    return this;
  }

  protected LinkedList<ModalStep> getSteps() {
    return steps;
  }

  protected ModalStep getFirstStep() {
    return steps.getFirst();
  }
}
