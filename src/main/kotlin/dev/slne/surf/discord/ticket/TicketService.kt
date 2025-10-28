package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val ticketChannel: TextChannel?,
    private val jda: JDA
) {
    suspend fun createTicket(hook: InteractionHook, type: TicketType): Boolean {

    }

    suspend fun hasTicket(userId: Long, ticketType: TicketType): Boolean {

    }

    suspend fun getTicket(userId: Long, ticketType: TicketType): Ticket? {

    }

    suspend fun deleteTicket(ticket: Ticket): Boolean {

    }
}