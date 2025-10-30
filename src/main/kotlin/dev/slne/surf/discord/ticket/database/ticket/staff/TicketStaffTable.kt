package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketStaffTable : LongIdTable("discord_ticket_data") {
    val ticketId = long("ticket_id").references(TicketTable.tickedId)
    val claimedAt = long("claimed_at").nullable()
    val claimedBy = long("claimed_by").nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()

    val watchedAt = long("watched_at").nullable()
    val watchedBy = long("watched_by").nullable()
    val watchedByName = varchar("watched_by_name", 100).nullable()
    val watchedByAvatar = varchar("watched_by_avatar", 200).nullable()
}