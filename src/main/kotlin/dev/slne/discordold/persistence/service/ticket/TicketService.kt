package dev.slne.discordold.persistence.service.ticket

import dev.slne.discordold.ticket.Ticket
import dev.slne.discordold.ticket.TicketType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service

@Service
class TicketService(private val ticketRepository: TicketRepository) {

    suspend fun hasAuthorTicketOfType(ticketType: TicketType, author: User) =
        withContext(Dispatchers.IO) {
            ticketRepository.hasAuthorTicketWithType(author.id, ticketType) > 0
        }

    suspend fun saveTicket(ticket: Ticket) = withContext(Dispatchers.IO) {
        ticketRepository.save(ticket)
    }

    suspend fun getTicketByThreadId(threadId: String) = withContext(Dispatchers.IO) {
        ticketRepository.findByThreadId(threadId)
    }

}
