package dev.slne.discord.discord.interaction.modal.step.creator.report.steps.player;

import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.ModalComponentBuilder;
import dev.slne.discord.discord.interaction.modal.step.ModalStep;
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
      TextInput.create(REPORT_PLAYER_NAME, "Dein Spielname", TextInputStyle.SHORT)
          .setRequired(true)
          .build()
    );
    builder.addComponent(
        TextInput.create(REPORT_PLAYER_REASON_INPUT, "Grund", TextInputStyle.PARAGRAPH)
            .setRequired(true)
            .setPlaceholder("Was hat der Spielende getan?")
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
    messages.addMessage("> Meldender Spielende: `%s`", reportPlayerName);
    messages.addMessage("");
    messages.addMessage("# Was hat der Spielende getan?");
    messages.addMessage("> %s", reportReason);
  }
}
