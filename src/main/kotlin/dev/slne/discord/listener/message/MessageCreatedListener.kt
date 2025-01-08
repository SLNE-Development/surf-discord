package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticket
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.util.toTicketMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class MessageCreatedListener(jda: JDA, ticketService: TicketService) {

    init {
        jda.listener<MessageReceivedEvent> { event ->
            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticket = event.channel.ticket ?: return@listener

            val message = event.message.toTicketMessage()
            ticket.addMessage(message)
            
            ticketService.saveTicket(ticket)
        }
    }
}
