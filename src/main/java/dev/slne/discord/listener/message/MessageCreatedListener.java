package dev.slne.discord.listener.message;

import java.util.Optional;

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

        Optional<Ticket> ticketOptional = DiscordBot.getInstance().getTicketManager().getTicket(channel.getId());

        if (ticketOptional.isEmpty()) {
            return;
        }

        Ticket ticket = ticketOptional.get();
        TicketMessage ticketMessage = new TicketMessage(ticket, message);

        ticket.addTicketMessage(ticketMessage);
    }

}
