package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

@Repository
class TicketRepository(
    private val ticketDataRepository: TicketDataRepository
) {
    suspend fun createTicket(ticket: Ticket) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.insert {
            it[ticketId] = ticket.ticketId
            it[authorId] = ticket.authorId
            it[authorName] = ticket.authorName
            it[authorAvatarUrl] = ticket.authorAvatar
            it[guildId] = ticket.guildId
            ticket.threadId?.let { id -> it[threadId] = id }
            it[ticketType] = ticket.ticketType
            it[openedAt] = ticket.createdAt
            it[closedAt] = ticket.closedAt
            it[closedById] = ticket.closedById
            it[createdAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun hasOpenTicket(authorId: Long, type: TicketType): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketTable.selectAll()
                .where(
                    (TicketTable.authorId eq authorId) and
                            (TicketTable.ticketType eq type) and
                            (TicketTable.closedAt.isNull())
                )
                .count() > 0
        }


    suspend fun getTicketByThreadId(threadId: Long): Ticket? =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketTable.selectAll().where(TicketTable.threadId eq threadId)
                .firstNotNullOfOrNull { it.toTicket() }
        }

    suspend fun markAsClosed(
        ticket: Ticket
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.update({ TicketTable.ticketId eq ticket.ticketId }) {
            it[TicketTable.closedAt] = ticket.closedAt
            it[TicketTable.closedById] = ticket.closedById
            it[TicketTable.closedByName] = ticket.closedByName
            it[TicketTable.closedByAvatarUrl] = ticket.closedByAvatar
            it[TicketTable.closedReason] = ticket.closedReason
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun getTicketById(ticketId: UUID): Ticket? = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.selectAll().where(TicketTable.ticketId eq ticketId)
            .firstNotNullOfOrNull { it.toTicket() }
    }

    suspend fun getTicket(authorId: Long, type: TicketType) =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketTable.selectAll()
                .where((TicketTable.authorId eq authorId) and (TicketTable.ticketType eq type))
                .firstNotNullOfOrNull { it.toTicket() }
        }

    private suspend fun ResultRow.toTicket(): Ticket {
        val id = this[TicketTable.ticketId]
        val data = ticketDataRepository.getData(id)

        return Ticket(
            ticketId = id,
            ticketData = data,
            authorId = this[TicketTable.authorId],
            authorName = this[TicketTable.authorName],
            authorAvatar = this[TicketTable.authorAvatarUrl],
            guildId = this[TicketTable.guildId],
            threadId = this[TicketTable.threadId],
            ticketType = this[TicketTable.ticketType],
            createdAt = this[TicketTable.openedAt],
            closedAt = this[TicketTable.closedAt],
            closedById = this[TicketTable.closedById],
            closedByName = this[TicketTable.closedByName],
            closedByAvatar = this[TicketTable.closedByAvatarUrl],
            closedReason = this[TicketTable.closedReason]
        )
    }
}