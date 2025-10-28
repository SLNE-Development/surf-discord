package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.TicketType
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository

@Repository
class TicketRepository {
    suspend fun createTicket(ticket: Ticket) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.insert {
            it[tickedId] = ticket.ticketId
            it[ticketData] = ticket.ticketData
            it[authorId] = ticket.authorId
            it[authorName] = ticket.authorName
            it[authorAvatarUrl] = ticket.authorAvatar
            it[guildId] = ticket.guildId
            it[threadId] = ticket.threadId
            it[ticketType] = ticket.ticketType
            it[createdAt] = ticket.createdAt
            it[closedAt] = ticket.closedAt
            it[closedById] = ticket.closedById
        }
    }

    suspend fun deleteTicket(ticketId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.deleteWhere { tickedId eq ticketId }
    }

    suspend fun getTicket(ticketId: Long): Ticket? = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.select(TicketTable.tickedId eq ticketId).firstNotNullOfOrNull { row ->
            Ticket(
                ticketId = row[TicketTable.tickedId],
                ticketData = row[TicketTable.ticketData],
                authorId = row[TicketTable.authorId],
                authorName = row[TicketTable.authorName],
                authorAvatar = row[TicketTable.authorAvatarUrl],
                guildId = row[TicketTable.guildId],
                threadId = row[TicketTable.threadId],
                ticketType = row[TicketTable.ticketType],
                createdAt = row[TicketTable.createdAt],
                closedAt = row[TicketTable.closedAt],
                closedById = row[TicketTable.closedById],
                closedByName = row[TicketTable.closedByName],
                closedByAvatar = row[TicketTable.closedByAvatarUrl],
                closedReason = row[TicketTable.closedReason]
            )
        }
    }

    suspend fun getTicket(authorId: Long, type: TicketType) =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketTable.select((TicketTable.authorId eq authorId) and (TicketTable.ticketType eq type))
                .firstNotNullOfOrNull { row ->
                    Ticket(
                        ticketId = row[TicketTable.tickedId],
                        ticketData = row[TicketTable.ticketData],
                        authorId = row[TicketTable.authorId],
                        authorName = row[TicketTable.authorName],
                        authorAvatar = row[TicketTable.authorAvatarUrl],
                        guildId = row[TicketTable.guildId],
                        threadId = row[TicketTable.threadId],
                        ticketType = row[TicketTable.ticketType],
                        createdAt = row[TicketTable.createdAt],
                        closedAt = row[TicketTable.closedAt],
                        closedById = row[TicketTable.closedById],
                        closedByName = row[TicketTable.closedByName],
                        closedByAvatar = row[TicketTable.closedByAvatarUrl],
                        closedReason = row[TicketTable.closedReason]
                    )
                }
        }
}