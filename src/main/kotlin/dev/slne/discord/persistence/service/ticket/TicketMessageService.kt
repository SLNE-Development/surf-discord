package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage

object TicketMessageService {

    suspend fun createTicketMessage(
        ticket: Ticket,
        ticketMessage: TicketMessage
    ): TicketMessage = TODO("Implement")
}
