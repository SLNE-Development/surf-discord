package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.Ticket
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class TicketStaffRepository(
    private val ticketRepository: TicketRepository
) {
    suspend fun claim(
        ticket: Ticket,
        claimer: User
    ) = suspendTransaction {
        val internalTicketId =
            ticketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction
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
            ticketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction false
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketId eq internalTicketId)
            .firstOrNull()

        staffEntry?.get(TicketStaffTable.claimedBy) == user.idLong
    }

    suspend fun isClaimed(
        ticket: Ticket
    ) = suspendTransaction {
        val internalTicketId =
            ticketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction false
        val staffEntry = TicketStaffTable.selectAll()
            .where(TicketStaffTable.ticketId eq internalTicketId)
            .firstOrNull()

        staffEntry?.get(TicketStaffTable.claimedAt) != null
    }

    suspend fun unclaim(
        ticket: Ticket
    ) = suspendTransaction {
        val internalTicketId =
            ticketRepository.getInternalId(ticket.ticketId) ?: return@suspendTransaction
        TicketStaffTable.update(where = { TicketStaffTable.ticketId eq internalTicketId }) {
            it[claimedAt] = null
            it[claimedBy] = null
            it[claimedByName] = null
            it[claimedByAvatar] = null
        }
    }
}