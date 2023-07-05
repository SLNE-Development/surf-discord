package dev.slne.discord.listener.message;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageUpdatedListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        MessageChannelUnion channel = event.getChannel();
        Message message = event.getMessage();

        if (!channel.getType().equals(ChannelType.TEXT)) {
            return;
        }

        if (event.getMessage().isWebhookMessage()) {
            return;
        }

        Ticket ticket = DiscordBot.getInstance().getTicketManager().getTicket(channel.getId());

        if (ticket == null) {
            return;
        }

        TicketMessage ticketMessage = ticket.getTicketMessage(message.getId());

        if (ticketMessage == null) {
            return;
        }

        ticketMessage.update(message).whenComplete(updatedTicketMessage -> {
            if (updatedTicketMessage == null) {
                return;
            }

            ticket.addRawTicketMessage(updatedTicketMessage);
        });
    }
}
