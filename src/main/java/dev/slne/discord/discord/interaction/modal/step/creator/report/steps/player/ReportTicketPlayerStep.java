package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
import dev.slne.discord.message.RawMessages;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class ReportTicketPlayerStep extends ModalStep {

  private static final String REPORT_PLAYER_REASON_INPUT = "report-player-reason";
  private static final String REPORT_PLAYER_NAME = "report-player-name";

  private String reportPlayerName, reportReason;

  @Override
  protected void buildModalComponents(ModalComponentBuilder builder) {
    builder.addFirstComponent(
        TextInput.create(
                REPORT_PLAYER_NAME,
                RawMessages.get("modal.report.step.player.input.reporting-player.label"),
                TextInputStyle.SHORT
            )
            .setRequired(true)
            .build()
    );
    builder.addComponent(
        TextInput.create(
                REPORT_PLAYER_REASON_INPUT,
                RawMessages.get("modal.report.step.player.input.reason.label"),
                TextInputStyle.PARAGRAPH
            )
            .setRequired(true)
            .setPlaceholder(RawMessages.get("modal.report.step.player.input.reason.placeholder"))
            .setMinLength(20)
            .build()
    );
  }

  @Override
  protected void verifyModalInput(ModalInteractionEvent event)
      throws ModalStepInputVerificationException {
    reportPlayerName = getRequiredInput(event, REPORT_PLAYER_NAME);
    reportReason = getRequiredInput(event, REPORT_PLAYER_REASON_INPUT);
  }

  @Override
  protected void buildOpenMessages(MessageQueue messages, TextChannel channel) {
    messages.addMessage(
        RawMessages.get("modal.report.step.player.messages.reporting-player", reportPlayerName));
    messages.addEmptyLine();
    messages.addMessage(
        "# " + RawMessages.get("modal.report.step.player.input.reason.placeholder"));
    messages.addMessage("> %s", reportReason);
  }
}
