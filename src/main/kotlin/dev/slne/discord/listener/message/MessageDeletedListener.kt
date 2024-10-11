package dev.slne.discord.listener.message

import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.function.Consumer

/**
 * The type Message deleted listener.
 */
@DiscordListener
class MessageDeletedListener @Autowired constructor(ticketService: TicketService?) :
    AbstractMessageListener<GenericMessageEvent?>(ticketService) {
    override fun onMessageDelete(@Nonnull event: MessageDeleteEvent) {
        deleteMessage(event.getChannel(), java.util.List.of(event.getMessageId()))
    }

    override fun onMessageBulkDelete(@Nonnull event: MessageBulkDeleteEvent) {
        deleteMessage(event.getChannel(), event.getMessageIds())
    }

    /**
     * Deletes the message from the ticket.
     *
     * @param channel    The channel the message was deleted from.
     * @param messageIds The message ids of the deleted messages.
     */
    @Async
    protected fun deleteMessage(channel: MessageChannel, messageIds: List<String>) {
        getTicketByChannel(channel).ifPresent(Consumer<Ticket> { ticket: Ticket ->
            messageIds.stream()
                .map<TicketMessage> { messageId: String? -> ticket.getTicketMessage(messageId) }  // Get the ticket message by id
                .filter { obj: TicketMessage? -> Objects.nonNull(obj) }
                .map<R> { obj: TicketMessage -> obj.delete() }  // Delete the message
                .map<Any> { obj: R -> obj.join() }
                .filter { obj: Any? -> Objects.nonNull(obj) }
                .forEach { ticketMessage: Any? -> ticket.addRawTicketMessage(ticketMessage) }
        }) // Add the raw message to the ticket
    }
}
