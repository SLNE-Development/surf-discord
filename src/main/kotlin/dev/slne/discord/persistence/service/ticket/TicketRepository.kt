package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.persistence.DiscordPersistence
import dev.slne.discord.ticket.Ticket

object TicketRepository {

    fun save(ticket: Ticket): Ticket {
        val entityManager = DiscordPersistence.entityManager

        entityManager.transaction.begin()

        if (ticket.id == null) {
            entityManager.persist(ticket)
        } else {
            entityManager.merge(ticket)
        }

        entityManager.transaction.commit()

        return ticket
    }

    fun findAll(): List<Ticket> {
        val entityManager = DiscordPersistence.entityManager

        val query = entityManager.createQuery(
            "SELECT t FROM Ticket t",
            Ticket::class.java
        )

        return query.resultList
    }

}