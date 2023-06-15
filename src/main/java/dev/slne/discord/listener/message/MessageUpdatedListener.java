package dev.slne.discord.listener.message;

import java.util.Optional;

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

        Optional<Ticket> ticketOptional = DiscordBot.getInstance().getTicketManager().getTicket(channel.getId());

        if (ticketOptional.isEmpty()) {
            return;
        }

        Ticket ticket = ticketOptional.get();
        Optional<TicketMessage> ticketMessageOptional = ticket.getTicketMessage(message.getId());

        if (ticketMessageOptional.isEmpty()) {
            return;
        }

        TicketMessage ticketMessage = ticketMessageOptional.get();
        ticketMessage.update(message).whenComplete(ticketMessageCallback -> {
            if (ticketMessageCallback.isEmpty()) {
                return;
            }

            TicketMessage createdTicketMessage = ticketMessageCallback.get();
            ticket.addRawTicketMessage(createdTicketMessage);
        });
    }
}
