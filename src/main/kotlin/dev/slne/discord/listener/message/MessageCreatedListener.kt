package dev.slne.discord.listener.message

import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Autowired

/**
 * The type Message created listener.
 */
@DiscordListener
class MessageCreatedListener @Autowired constructor(ticketService: TicketService?) :
    AbstractMessageListener<MessageReceivedEvent>(ticketService) {
    override fun onMessageReceived(@Nonnull event: MessageReceivedEvent) {
        processEvent(event)
    }

    override fun handleEvent(event: MessageReceivedEvent, ticket: Ticket) {
        if (event.getMessage().isWebhookMessage()) {
            return
        }

        ticket.addTicketMessage(TicketMessage.fromTicketAndMessage(ticket, event.getMessage()))
    }
}
