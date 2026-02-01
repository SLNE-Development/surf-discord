package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.column.zonedDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object TicketStaffTable : LongIdTable("ticket_staff") {
    val ticketId = nativeUuid("ticket_id").uniqueIndex()
    val claimedAt = zonedDateTime("claimed_at").nullable()
    val claimedBy = long("claimed_by").nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()
}