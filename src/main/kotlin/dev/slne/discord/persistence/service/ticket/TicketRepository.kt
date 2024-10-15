package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.persistence.findAll
import dev.slne.discord.persistence.sessionFactory
import dev.slne.discord.persistence.upsert
import dev.slne.discord.persistence.withSession
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketDto

object TicketRepository {

    suspend fun save(ticket: Ticket): Ticket = sessionFactory.withSession { session ->
//        if (ticket.id == null) {
//            session.persist(ticket)
//            ticket
//        } else {
//            session.merge(ticket)
//        }
        session.upsert(ticket) { id != null }
    }

    suspend fun findAll(): List<Ticket> =
        sessionFactory.withSession<List<TicketDto>> { it.findAll() }
            .map { it.toTicket() }
}