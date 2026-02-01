package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object TicketDataTable : LongIdTable("ticket_data") {
    val ticketId = nativeUuid("ticket_id")
    val dataKey = varchar("data_key", 100)
    val dataValue = largeText("data_value")
}