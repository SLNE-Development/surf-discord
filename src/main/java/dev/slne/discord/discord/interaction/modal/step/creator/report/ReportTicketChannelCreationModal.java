package dev.slne.discord.discord.interaction.modal.step.creator.report;

import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.ReportTicketSelectTypeStep;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ReportTicketChannelCreationModal extends DiscordStepChannelCreationModal {

  public ReportTicketChannelCreationModal() {
    super("surf-report-ticket-modal", TicketType.REPORT.getName() + " erstellen",
        TicketType.REPORT);
  }

  @Override
  protected StepBuilder buildSteps() {
    return StepBuilder.startWith(new ReportTicketSelectTypeStep());
  }

  @Override
  protected void getOpenMessages(MessageQueue messages, TextChannel channel, User user) {
    messages.addMessage(user.getAsMention());
    messages.addMessage("Danke für deinen Report! Wir haben deine Informationen erhalten und werden "
        + "uns bald darum kümmern. Bitte hab etwas Geduld, während wir den Vorfall untersuchen.");
  }
}
