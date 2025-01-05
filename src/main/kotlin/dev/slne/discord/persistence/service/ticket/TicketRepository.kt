package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.persistence.sessionFactory
import dev.slne.discord.persistence.upsert
import dev.slne.discord.persistence.withSession
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage

object TicketRepository {

    suspend fun save(ticket: Ticket): Ticket = sessionFactory.withSession { session ->
        session.upsert(ticket) { id != null }
    }

    suspend fun findActive(): List<Ticket> = sessionFactory.withSession { session ->
        val criteriaBuilder = session.criteriaBuilder

        val query = criteriaBuilder.createQuery(Ticket::class.java)
        val root = query.from(Ticket::class.java)
        query.where(criteriaBuilder.isNull(root.get<Ticket>("closedAt")))

        session.createQuery(query.select(root)).resultList
    }

    suspend fun findByThreadId(threadId: String): Ticket? = sessionFactory.withSession { session ->
        val criteriaBuilder = session.criteriaBuilder

        val query = criteriaBuilder.createQuery(Ticket::class.java)
        val root = query.from(Ticket::class.java)
        query.where(criteriaBuilder.equal(root.get<String>("threadId"), threadId))

        session.createQuery(query.select(root)).resultList.firstOrNull()
    }

    suspend fun saveMessage(message: TicketMessage): TicketMessage =
        sessionFactory.withSession { session ->
            session.upsert(message) { id != null }
        }
}