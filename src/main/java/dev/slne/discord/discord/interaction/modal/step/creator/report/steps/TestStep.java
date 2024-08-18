package dev.slne.discord.discord.interaction.modal.step.creator.report.steps;

import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;

public class TestStep extends ModalSelectionStep {

  public TestStep() {
    super("Test",
        SelectOption.of("Test", "test")
            .withDescription("Test"),
        SelectOption.of("Test2", "test2")
            .withDescription("Test2")
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
  }

  @Override
  public StepBuilder buildChildSteps() {
    return StepBuilder.startWith(new ReportTicketSelectTypeStep());
  }
}
