package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.persistence.sessionFactory
import dev.slne.discord.persistence.withSession
import dev.slne.discord.ticket.Ticket

object TicketRepository {

    suspend fun save(ticket: Ticket): Ticket = sessionFactory.withSession { session ->
        if (ticket.id == null) {
            session.persist(ticket)
            ticket
        } else {
            session.merge(ticket)
        }
    }

    suspend fun findAll(): List<Ticket> = sessionFactory.withSession { session ->
        session.createQuery("SELECT t FROM Ticket t", Ticket::class.java).resultList
    }
}