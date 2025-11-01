package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TicketRepository(
    private val ticketDataRepository: TicketDataRepository
) {
    suspend fun createTicket(ticket: Ticket) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.insert {
            it[ticketUid] = ticket.ticketUid
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
                .firstNotNullOfOrNull { row ->
                    val id = row[TicketTable.ticketUid]
                    val data = ticketDataRepository.getData(id)
                    Ticket(
                        ticketUid = id,
                        ticketData = data,
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

    suspend fun markAsClosed(
        ticket: Ticket
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.update({ TicketTable.ticketUid eq ticket.ticketUid }) {
            it[TicketTable.closedAt] = ticket.closedAt
            it[TicketTable.closedById] = ticket.closedById
            it[TicketTable.closedByName] = ticket.closedByName
            it[TicketTable.closedByAvatarUrl] = ticket.closedByAvatar
            it[TicketTable.closedReason] = ticket.closedReason
        }
    }

    suspend fun getTicketById(ticketUid: UUID): Ticket? = newSuspendedTransaction(Dispatchers.IO) {
        TicketTable.selectAll().where(TicketTable.ticketUid eq ticketUid)
            .firstNotNullOfOrNull { row ->
                val id = row[TicketTable.ticketUid]
                val data = ticketDataRepository.getData(id)
                Ticket(
                    ticketUid = id,
                    ticketData = data,
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
            TicketTable.selectAll()
                .where((TicketTable.authorId eq authorId) and (TicketTable.ticketType eq type))
                .firstNotNullOfOrNull { row ->
                    val id = row[TicketTable.ticketUid]
                    val data = ticketDataRepository.getData(id)
                    Ticket(
                        ticketUid = id,
                        ticketData = data,
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