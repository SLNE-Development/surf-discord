package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {

    @Cacheable(value = ["tickets"])
    fun findByClosedAtNull(): List<Ticket>

    @Cacheable(value = ["tickets"])
    fun findByThreadId(threadId: String): Ticket?
}