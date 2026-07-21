package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable

object TicketDataTable : LongIdTable("ticket_data") {
    val ticketId = ulong("ticket_id").references(TicketTable.id)
    val dataKey = varchar("data_key", 100)
    val dataValue = largeText("data_value")
}