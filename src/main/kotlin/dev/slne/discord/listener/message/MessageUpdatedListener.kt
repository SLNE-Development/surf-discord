package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticketOrNull
import dev.slne.discord.persistence.service.ticket.TicketService
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.springframework.stereotype.Component

@Component
class MessageUpdatedListener(private val jda: JDA, private val ticketService: TicketService) {

    @PostConstruct
    fun registerListener() {
        jda.listener<MessageUpdateEvent> { event ->
            val ticket = event.channel.ticketOrNull() ?: return@listener

            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticketMessage = ticket.getTicketMessage(event.messageId) ?: return@listener
            ticket.addMessage(ticketMessage.copyAndUpdate(event.message))

            ticketService.saveTicket(ticket)
        }
    }
}
