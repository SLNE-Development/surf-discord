package dev.slne.discord.discord.interaction.modal.step.creator.report.steps;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalSelectionStep;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.griefing.ReportTicketGriefingStep;
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player.ReportTicketPlayerStep;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ReportTicketSelectTypeStep extends ModalSelectionStep {

  private static final String OPTION_GRIEFING = "griefing";
  private static final String OPTION_PLAYER = "player";

  private static final String REPORTING_PLAYER_NAME_INPUT = "player-name";

  private String playerName;

  public ReportTicketSelectTypeStep() {
    super(
        RawMessages.get("modal.report.step.type.selection.title"),
        SelectOption.of(
                RawMessages.get("modal.report.step.type.selection.griefing.label"),
                OPTION_GRIEFING
            )
            .withDescription(
                RawMessages.get("modal.report.step.type.selection.griefing.description")
            ),
        SelectOption.of(
                RawMessages.get("modal.report.step.type.selection.player.label"),
                OPTION_PLAYER
            )
            .withDescription(
                RawMessages.get("modal.report.step.type.selection.player.description")
            )
    );
  }

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    final String label;

    if (isGriefing()) {
      label = RawMessages.get("modal.report.step.type.input.griefing.own-name");
    } else {
      label = RawMessages.get("modal.report.step.type.input.report.reported-player");
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
    messages.addEmptyLine();
    messages.addMessage(RawMessages.get("modal.report.step.type.messages.player-name", playerName));
  }

  @Override
  public StepBuilder buildChildSteps() {
    if (isGriefing()) {
      return StepBuilder.startWith(new ReportTicketGriefingStep());
    } else if (isPlayer()) {
      return StepBuilder.startWith(new ReportTicketPlayerStep());
    } else {
      return StepBuilder.empty();
    }
  }

  private boolean isGriefing() {
    return OPTION_GRIEFING.equals(getSelected());
  }

  private boolean isPlayer() {
    return OPTION_PLAYER.equals(getSelected());
  }
}
