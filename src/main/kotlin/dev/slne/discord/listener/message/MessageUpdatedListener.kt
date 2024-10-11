package dev.slne.discord.listener.message

import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.springframework.beans.factory.annotation.Autowired

/**
 * The type Message updated listener.
 */
@DiscordListener
class MessageUpdatedListener @Autowired constructor(ticketService: TicketService?) :
    AbstractMessageListener<MessageUpdateEvent>(ticketService) {
    @Async
    override fun handleEvent(event: MessageUpdateEvent, ticket: Ticket) {
        if (event.getMessage().isWebhookMessage()) {
            return
        }

        val ticketMessage: TicketMessage = ticket.getTicketMessage(event.getMessageId())

        if (ticketMessage == null) {
            return
        }

        val updatedTicketMessage: TicketMessage? = ticketMessage.update(event.getMessage()).join()

        if (updatedTicketMessage != null) {
            ticket.addRawTicketMessage(updatedTicketMessage)
        }
    }

    override fun onMessageUpdate(@Nonnull event: MessageUpdateEvent) {
        processEvent(event)
    }
}
