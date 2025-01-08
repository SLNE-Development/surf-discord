package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticket
import dev.slne.discord.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageUpdateEvent

class MessageUpdatedListener(jda: JDA, ticketService: TicketService) {

    init {
        jda.listener<MessageUpdateEvent> { event ->
            val ticket = event.channel.ticket ?: return@listener

            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticketMessage = ticket.getTicketMessage(event.messageId) ?: return@listener
            ticket.addMessage(ticketMessage.copyAndUpdate(event.message))

            ticketService.saveTicket(ticket)
        }
    }
}
