package dev.slne.discord.listener.message;

import dev.slne.data.api.DataApi;
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

import javax.annotation.Nonnull;
import java.util.List;

public class MessageDeletedListener extends ListenerAdapter {

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        MessageChannelUnion channel = event.getChannel();
        String messageId = event.getMessageId();

        deleteMessage(channel, List.of(messageId));
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

            ticketMessage.delete().thenAcceptAsync(deletedTicketMessage -> {
                if (deletedTicketMessage == null) {
                    return;
                }

                ticket.addRawTicketMessage(deletedTicketMessage);
            }).exceptionally(throwable -> {
                DataApi.getDataInstance().logError(getClass(), "Failed to delete ticket message", throwable);
                return null;
            });
        }
    }

}
