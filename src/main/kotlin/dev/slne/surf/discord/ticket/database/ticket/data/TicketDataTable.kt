package dev.slne.surf.discord.ticket.database.ticket.data

import org.jetbrains.exposed.dao.id.LongIdTable

object TicketDataTable : LongIdTable("ticket_data") {
    val ticketId = uuid("ticket_id")
    val dataKey = varchar("data_key", 100)
    val dataValue = largeText("data_value")
}