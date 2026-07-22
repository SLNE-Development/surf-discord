package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.isNull
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.update
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.ticket.database.ticket.data.TicketDataRepository
import dev.slne.surf.discord.ticket.database.ticket.staff.TicketStaffTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TicketRepository(
    private val ticketDataRepository: TicketDataRepository
) {
    suspend fun createTicket(ticket: Ticket) = suspendTransaction {
        TicketTable.insert {
            it[ticketId] = ticket.ticketId
            it[authorId] = ticket.authorId
            it[authorName] = ticket.authorName
            it[authorAvatarUrl] = ticket.authorAvatar
            it[guildId] = ticket.guildId
            ticket.threadId?.let { id -> it[threadId] = id }
            it[ticketType] = ticket.ticketType
            it[openedAt] = ticket.createdAt
        }
    }

    suspend fun getOpenTickets() = suspendTransaction {
        TicketTable.selectAll()
            .where(TicketTable.closedAt.isNull())
            .filterNotNull()
            .map { it.toTicket() }
            .toList()
    }

    suspend fun getInternalId(ticketId: UUID) = suspendTransaction {
        TicketTable.selectAll().where(TicketTable.ticketId eq ticketId)
            .map { it[TicketTable.id].value }
            .firstOrNull()
    }

    suspend fun hasOpenTicket(authorId: Long, type: TicketType): Boolean =
        suspendTransaction {
            TicketTable.selectAll()
                .where(
                    (TicketTable.authorId eq authorId) and
                            (TicketTable.ticketType eq type) and
                            (TicketTable.closedAt.isNull())
                )
                .count() > 0
        }


    suspend fun getTicketByThreadId(threadId: Long): Ticket? =
        suspendTransaction {
            println("Fetching ticket by threadId: $threadId")
            TicketTable.selectAll().where(TicketTable.threadId eq threadId).filterNotNull()
                .map { it.toTicket() }.firstOrNull()
        }

    suspend fun markAsClosed(
        ticket: Ticket
    ) = suspendTransaction {
        TicketTable.update({ TicketTable.ticketId eq ticket.ticketId }) {
            it[TicketTable.closedAt] = ticket.closedAt
            it[TicketTable.closedById] = ticket.closedById
            it[TicketTable.closedByName] = ticket.closedByName
            it[TicketTable.closedByAvatarUrl] = ticket.closedByAvatar
            it[TicketTable.closedReason] = ticket.closedReason
        }
    }

    suspend fun getTicketById(ticketId: UUID): Ticket? = suspendTransaction {
        TicketTable.selectAll().where(TicketTable.ticketId eq ticketId)
            .filterNotNull().map { it.toTicket() }.firstOrNull()
    }

    suspend fun getTicket(authorId: Long, type: TicketType) =
        suspendTransaction {
            TicketTable.selectAll()
                .where((TicketTable.authorId eq authorId) and (TicketTable.ticketType eq type))
                .filterNotNull().map { it.toTicket() }.firstOrNull()
        }

    suspend fun getOpenTicketsClaimedBy(userId: Long): List<Ticket> = suspendTransaction {
        (TicketTable innerJoin TicketStaffTable)
            .selectAll()
            .where(
                (TicketStaffTable.claimedBy eq userId) and
                        (TicketTable.closedAt.isNull())
            )
            .filterNotNull()
            .map { it.toTicket() }
            .toList()
    }

    private suspend fun ResultRow.toTicket(): Ticket {
        val id = this[TicketTable.ticketId]
        val internalId = this[TicketTable.id].value
        val data = ticketDataRepository.getData(internalId)

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
        ).apply {
            this.internalTicketId = internalId
        }
    }
}