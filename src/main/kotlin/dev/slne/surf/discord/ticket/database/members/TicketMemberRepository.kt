package dev.slne.surf.discord.ticket.database.members

import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

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
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketMemberTable.upsert {
            it[ticketId] = ticket.ticketId
            it[memberId] = userId
            it[memberName] = userName
            it[memberAvatarUrl] = userAvatarUrl
            it[addedAt] = System.currentTimeMillis()
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
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMemberTable.update({ (TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.memberId eq removedById) and (TicketMemberTable.removedAt.isNull()) }) {
                it[removedAt] = System.currentTimeMillis()
                it[this.removedById] = removedById
                it[this.removedByName] = removedByName
                it[this.removedByAvatarUrl] = removedByAvatarUrl
            }
        }

    suspend fun getMembers(ticket: Ticket): List<Long> = newSuspendedTransaction(Dispatchers.IO) {
        TicketMemberTable.selectAll()
            .where((TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.removedAt.isNull()))
            .map { it[TicketMemberTable.memberId] }
    }

    suspend fun isMember(ticket: Ticket, userId: Long): Boolean =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMemberTable.selectAll()
                .where((TicketMemberTable.ticketId eq ticket.ticketId) and (TicketMemberTable.memberId eq userId) and (TicketMemberTable.removedAt.isNull()))
                .count() > 0
        }
}