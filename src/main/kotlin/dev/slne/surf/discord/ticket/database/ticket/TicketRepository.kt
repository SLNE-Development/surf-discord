package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.TicketType
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository

@Repository
class TicketRepository {
    suspend fun createTicket(ticket: Ticket) = newSuspendedTransaction(Dispatchers.IO) {
        logger.debug("Creating ticket with ID ${ticket.ticketId} for author ${ticket.authorId}")
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

    suspend fun updateData(ticket: Ticket, data: String) = newSuspendedTransaction(Dispatchers.IO) {
        logger.debug("Updating ticket data for ticket ID ${ticket.ticketId}")
        TicketTable.update({ TicketTable.tickedId eq ticket.ticketId }) {
            it[ticketData] = data
        }
    }

    suspend fun getTicketByThreadId(threadId: Long): Ticket? =
        newSuspendedTransaction(Dispatchers.IO) {
            logger.debug("Fetching ticket by thread ID $threadId")
            TicketTable.selectAll().where(TicketTable.threadId eq threadId)
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

    suspend fun deleteTicket(ticketId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        logger.debug("Deleting ticket with ID $ticketId")
        TicketTable.deleteWhere { tickedId eq ticketId }
    }

    suspend fun getTicketById(ticketId: Long): Ticket? = newSuspendedTransaction(Dispatchers.IO) {
        logger.debug("Fetching ticket by ID $ticketId")
        TicketTable.selectAll().where(TicketTable.tickedId eq ticketId)
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

    suspend fun getTicket(authorId: Long, type: TicketType) =
        newSuspendedTransaction(Dispatchers.IO) {
            logger.debug("Fetching ticket for author ID $authorId and type $type")
            TicketTable.selectAll()
                .where((TicketTable.authorId eq authorId) and (TicketTable.ticketType eq type))
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