package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class TicketStaffRepository {
    suspend fun claim(
        ticket: Ticket,
        claimer: User
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val existing = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketUid eq ticket.ticketUid)
            .firstOrNull()

        if (existing == null) {
            TicketStaffTable.insert {
                it[ticketUid] = ticket.ticketUid
                it[claimedAt] = System.currentTimeMillis()
                it[claimedBy] = claimer.idLong
                it[claimedByName] = claimer.name
                it[claimedByAvatar] = claimer.avatarUrl
            }
        } else {
            // Nur Claim-Felder updaten
            TicketStaffTable.update({ TicketStaffTable.ticketUid eq ticket.ticketUid }) {
                it[claimedAt] = System.currentTimeMillis()
                it[claimedBy] = claimer.idLong
                it[claimedByName] = claimer.name
                it[claimedByAvatar] = claimer.avatarUrl
            }
        }
    }


    suspend fun isClaimedByUser(
        ticket: Ticket,
        user: User
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketUid eq ticket.ticketUid)
            .firstOrNull()
        
        staffEntry?.get(TicketStaffTable.claimedBy) == user.idLong
    }

    suspend fun isClaimed(
        ticket: Ticket
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketUid eq ticket.ticketUid)
            .firstOrNull()

        staffEntry?.get(TicketStaffTable.claimedAt) != null
    }

    suspend fun unclaim(
        ticket: Ticket
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketStaffTable.update(where = { TicketStaffTable.ticketUid eq ticket.ticketUid }) {
            it[claimedAt] = null
            it[claimedBy] = null
            it[claimedByName] = null
            it[claimedByAvatar] = null
        }
    }
}