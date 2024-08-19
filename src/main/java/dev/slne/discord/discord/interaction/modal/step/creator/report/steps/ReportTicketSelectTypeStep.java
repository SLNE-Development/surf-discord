package dev.slne.discord.discord.interaction.modal.step.creator.report.steps;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing.ReportTicketGriefingStep;
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player.ReportTicketPlayerStep;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ReportTicketSelectTypeStep extends ModalSelectionStep {

  private static final SelectOption GRIEFING = SelectOption.of("Griefing", "griefing");
  private static final SelectOption PLAYER = SelectOption.of("Spieler", "player");

  private static final String REPORTING_PLAYER_NAME_INPUT = "player-name";

  private String playerName;

  public ReportTicketSelectTypeStep() {
    super(
        "Möchtest du Griefing oder einen Spieler Melden?",
        GRIEFING
            .withDescription("Du wurdest gegrieft? Dann wähle diese Option!"),
        PLAYER
            .withDescription("Du möchtest einen Spieler melden? Dann wähle diese Option!")
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    final String label;

    if (GRIEFING.getValue().equals(getSelected())) {
      label = "Dein Spielername";
    } else {
      label = "Zu meldender Spielername";
    }

    builder.addComponent(
        TextInput.create(REPORTING_PLAYER_NAME_INPUT, label, TextInputStyle.SHORT)
            .setRequired(true)
            .setRequiredRange(3, 16)
            .setPlaceholder("Notch")
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    playerName = getRequiredInput(event, REPORTING_PLAYER_NAME_INPUT);
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage("");
    messages.addMessage("> Spielername: `%s`", playerName);
  }

  @Override
  public StepBuilder buildChildSteps() {
    if (GRIEFING.getValue().equals(getSelected())) {
      return StepBuilder.startWith(new ReportTicketGriefingStep());
    } else if (PLAYER.getValue().equals(getSelected())) {
      return StepBuilder.startWith(new ReportTicketPlayerStep());
    } else {
      return StepBuilder.empty();
    }
  }
}
