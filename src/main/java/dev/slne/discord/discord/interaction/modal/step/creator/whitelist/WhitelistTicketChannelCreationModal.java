package dev.slne.discord.discord.interaction.modal.step.creator.whitelist;

import dev.slne.discord.annotation.ChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal;
import dev.slne.discord.discord.interaction.modal.step.MessageQueue;
import dev.slne.discord.discord.interaction.modal.step.StepBuilder;
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected;
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep;
import dev.slne.discord.message.RawMessages;
import dev.slne.discord.ticket.TicketType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@ChannelCreationModal(ticketType = TicketType.WHITELIST)
public class WhitelistTicketChannelCreationModal extends DiscordStepChannelCreationModal {

  protected WhitelistTicketChannelCreationModal() {
    super(RawMessages.get("modal.whitelist.title"));
  }

  @Override
  protected StepBuilder buildSteps() {
    return StepBuilder.startWith(new WhitelistTicketConfirmTwitchConnected())
        .then(WhitelistTicketMinecraftNameStep::new);
  }

  @Override
  protected void getOpenMessages(MessageQueue messages, TextChannel channel, User user) {
    messages.addMessage(user.getAsMention());
  }
}
