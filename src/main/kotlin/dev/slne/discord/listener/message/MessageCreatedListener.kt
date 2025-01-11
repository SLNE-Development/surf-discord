package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticketOrNull
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.util.toTicketMessage
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
