package dev.slne.surf.discord.ticket.database.ticket.data

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.java.javaUUID

object TicketDataTable : LongIdTable("ticket_data") {
    val ticketId = javaUUID("ticket_id")
    val dataKey = varchar("data_key", 100)
    val dataValue = largeText("data_value")
}