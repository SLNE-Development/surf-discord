package dev.slne.discord.listener.message;

import dev.slne.data.api.DataApi;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

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

        ticketMessage.update(message).thenAcceptAsync(updatedTicketMessage -> {
            if (updatedTicketMessage == null) {
                return;
            }

            ticket.addRawTicketMessage(updatedTicketMessage);
        }).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(), "Failed to update ticket message.", throwable);
            return null;
        });
    }
}
