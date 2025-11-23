package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketStaffTable : LongIdTable("discord_ticket_staff") {
    val ticketId = uuid("ticket_id").references(TicketTable.ticketId).uniqueIndex()
    val claimedAt = long("claimed_at").nullable()
    val claimedBy = long("claimed_by").nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()
}