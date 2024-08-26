package dev.slne.discord.discord.interaction.modal.step.creator.unban;

import dev.slne.discord.annotation.ChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketPunishmentIdStep;
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketUnbanAppealStep;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@ChannelCreationModal(ticketType = TicketType.UNBAN)
public class UnbanTicketChannelCreationModal extends DiscordStepChannelCreationModal {

  public UnbanTicketChannelCreationModal() {
    super(TicketType.UNBAN.getName() + "  erstellen");
  }

  @Override
  protected StepBuilder buildSteps() {
    return StepBuilder.startWith(new UnbanTicketPunishmentIdStep())
        .then(UnbanTicketUnbanAppealStep::new);
  }

  @Override
  protected void getOpenMessages(MessageQueue messages, TextChannel channel, User user) {
    messages.addMessage(user.getAsMention());
    messages.addMessage("Wir haben deine Anfrage erhalten. Bitte beachte, dass die Bearbeitung von "
                        + "Entbannungsanträgen während laufender Events **bis zu 24 Stunden** dauern kann!");
  }
}
