package dev.slne.surf.discord.ticket.database.ticket.staff

import dev.slne.surf.database.columns.time.zonedDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.util.schemedName

object TicketStaffTable : LongIdTable(schemedName("ticket_staff")) {
    val ticketId = ulong("ticket_id").references(TicketTable.id)
    val claimedAt = zonedDateTime("claimed_at").nullable()
    val claimedBy =
        varchar("claimed_by", 20).transform({ it.toLong() }, { it.toString() }).nullable()
    val claimedByName = varchar("claimed_by_name", 100).nullable()
    val claimedByAvatar = varchar("claimed_by_avatar", 200).nullable()
}