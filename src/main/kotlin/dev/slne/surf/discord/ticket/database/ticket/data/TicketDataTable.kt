package dev.slne.surf.discord.ticket.database.ticket.data

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketDataTable : LongIdTable("discord_ticket_data") {
    val ticketId = reference("ticket_id", TicketTable)
    val dataKey = varchar("data_key", 100)
    val dataValue = largeText("data_value")
}