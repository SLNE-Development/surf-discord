package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {

    fun findByThreadId(threadId: String): Ticket?

    @Query("select count(t) from Ticket t where t.ticketAuthorId = ?1 and t.ticketType = ?2 and t.closedAt is null")
    fun hasAuthorTicketWithType(ticketAuthorId: String, ticketType: TicketType): Long

}