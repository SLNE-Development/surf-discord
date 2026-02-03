package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.discord.ticket.database.column.zonedDateTime
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object TicketStaffTable : LongIdTable("ticket_staff") {
    val ticketId = ulong("ticket_id").references(TicketTable.id)
    val claimedAt = zonedDateTime("claimed_at").nullable()
    val claimedBy =
        char("claimed_by", 20).transform({ it.toLong() }, { it.toString() }).nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()
}