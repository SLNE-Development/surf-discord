package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.update
import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.User
import java.time.ZonedDateTime

object TicketStaffRepository {
    suspend fun claim(
        ticket: Ticket,
        claimer: User
    ) = suspendTransaction {
        val internalTicketId =
            TicketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction
        val existing = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketId eq internalTicketId)
            .firstOrNull()

        if (existing == null) {
            TicketStaffTable.insert {
                it[ticketId] = internalTicketId
                it[claimedAt] = ZonedDateTime.now()
                it[claimedBy] = claimer.idLong
                it[claimedByName] = claimer.name
                it[claimedByAvatar] = claimer.avatarUrl
            }
        } else {
            TicketStaffTable.update({ TicketStaffTable.ticketId eq internalTicketId }) {
                it[claimedAt] = ZonedDateTime.now()
                it[claimedBy] = claimer.idLong
                it[claimedByName] = claimer.name
                it[claimedByAvatar] = claimer.avatarUrl
            }
        }
    }


    suspend fun isClaimedByUser(
        ticket: Ticket,
        user: User
    ) = suspendTransaction {
        val internalTicketId =
            TicketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction false
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketId eq internalTicketId)
            .firstOrNull()

        staffEntry?.get(TicketStaffTable.claimedBy) == user.idLong
    }

    suspend fun isClaimed(
        ticket: Ticket
    ) = suspendTransaction {
        val internalTicketId =
            TicketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction false
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketId eq internalTicketId)
            .firstOrNull()

        staffEntry?.get(TicketStaffTable.claimedAt) != null
    }

    suspend fun unclaim(
        ticket: Ticket
    ) = suspendTransaction {
        val internalTicketId =
            TicketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction
        TicketStaffTable.update(where = { TicketStaffTable.ticketId eq internalTicketId }) {
            it[claimedAt] = null
            it[claimedBy] = null
            it[claimedByName] = null
            it[claimedByAvatar] = null
        }
    }
}