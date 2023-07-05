package dev.slne.discord.listener.message;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageCreatedListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        MessageChannelUnion channel = event.getChannel();
        Message message = event.getMessage();

        if (!channel.getType().equals(ChannelType.TEXT)) {
            return;
        }

        Ticket ticket = DiscordBot.getInstance().getTicketManager().getTicket(channel.getId());

        if (ticket == null) {
            return;
        }

        if (event.getMessage().isWebhookMessage()) {
            return;
        }

        TicketMessage ticketMessage = new TicketMessage(ticket, message);

        ticket.addTicketMessage(ticketMessage);
    }

}
