package dev.slne.discord.listener.message;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class MessageDeletedListener extends ListenerAdapter {

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        MessageChannelUnion channel = event.getChannel();
        String messageId = event.getMessageId();

        deleteMessage(channel, Arrays.asList(messageId));
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        GuildMessageChannelUnion channel = event.getChannel();
        List<String> messageIds = event.getMessageIds();

        deleteMessage(channel, messageIds);
    }

    /**
     * Deletes the message from the ticket.
     *
     * @param channel    The channel the message was deleted from.
     * @param messageIds The message ids of the deleted messages.
     */
    @SuppressWarnings("java:S135")
    private void deleteMessage(MessageChannel channel, List<String> messageIds) {
        if (!channel.getType().equals(ChannelType.TEXT)) {
            return;
        }

        for (String messageId : messageIds) {
            Ticket ticket = DiscordBot.getInstance().getTicketManager().getTicket(channel.getId());

            if (ticket == null) {
                continue;
            }

            TicketMessage ticketMessage = ticket.getTicketMessage(messageId);

            if (ticketMessage == null) {
                continue;
            }

            RestAction<Message> message = ticketMessage.getMessage();

            if (message == null) {
                continue;
            }

            ticketMessage.delete().whenComplete(deletedTicketMessage -> {
                if (deletedTicketMessage == null) {
                    return;
                }

                ticket.addRawTicketMessage(deletedTicketMessage);
            });
        }
    }

}
