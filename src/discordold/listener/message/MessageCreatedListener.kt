package dev.slne.discordold.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discordold.extensions.ticketOrNull
import dev.slne.discordold.persistence.service.ticket.TicketService
import dev.slne.discordold.util.toTicketMessage
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class MessageCreatedListener(private val jda: JDA, private val ticketService: TicketService) {

    @PostConstruct
    fun registerListener() {
        jda.listener<MessageReceivedEvent> { event ->
            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticket = event.channel.ticketOrNull() ?: return@listener

            val message = event.message.toTicketMessage()
            ticket.addMessage(message)

            ticketService.saveTicket(ticket)
        }
    }
}
