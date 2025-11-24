package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketStaffTable : LongIdTable("ticket_staff") {
    val ticketId = uuid("ticket_id").uniqueIndex()
    val claimedAt = zonedDateTime("claimed_at").nullable()
    val claimedBy = long("claimed_by").nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()
}