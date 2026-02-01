package dev.slne.surf.discord.ticket.database.members

import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.jetbrains.exposed.v1.r2dbc.upsert
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class TicketMemberRepository {
    suspend fun addMember(
        ticket: Ticket,
        userId: Long,
        userName: String,
        userAvatarUrl: String?,
        addedById: Long,
        addedByName: String,
        addedByAvatarUrl: String?
    ) = suspendTransaction {
        TicketMemberTable.upsert {
            it[ticketId] = ticket.ticketId
            it[memberId] = userId
            it[memberName] = userName
            it[memberAvatarUrl] = userAvatarUrl
            it[addedAt] = ZonedDateTime.now()
            it[createdAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
            it[this.addedById] = addedById
            it[this.addedByName] = addedByName
            it[this.addedByAvatarUrl] = addedByAvatarUrl

            it[this.removedAt] = null
            it[this.removedById] = null
            it[this.removedByName] = null
            it[this.removedByAvatarUrl] = null
        }
    }

    suspend fun removeMember(
        ticket: Ticket,
        removedById: Long,
        removedByName: String,
        removedByAvatarUrl: String?
    ) =
        suspendTransaction {
            TicketMemberTable.update({ (TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.memberId eq removedById) and (TicketMemberTable.removedAt.isNull()) }) {
                it[removedAt] = ZonedDateTime.now()
                it[this.removedById] = removedById
                it[this.removedByName] = removedByName
                it[this.removedByAvatarUrl] = removedByAvatarUrl
                it[updatedAt] = ZonedDateTime.now()
            }
        }

    suspend fun getMembers(ticket: Ticket): List<Long> = suspendTransaction {
        TicketMemberTable.selectAll()
            .where((TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.removedAt.isNull()))
            .map { it[TicketMemberTable.memberId] }.toList()
    }

    suspend fun isMember(ticket: Ticket, userId: Long): Boolean =
        suspendTransaction {
            TicketMemberTable.selectAll()
                .where((TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.memberId eq userId) and (TicketMemberTable.removedAt.isNull()))
                .count() > 0
        }
}