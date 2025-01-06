package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : CoroutineCrudRepository<Ticket, Long> {
    
    @Cacheable(value = ["tickets"])
    fun findByClosedAtNull(): List<Ticket>

    @Cacheable(value = ["tickets"], key = "#threadId")
    fun findByThreadId(threadId: String): Ticket?
}