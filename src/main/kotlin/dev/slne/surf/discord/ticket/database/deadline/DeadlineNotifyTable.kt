package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.discord.ticket.database.util.AuditableLongIdTable
import dev.slne.surf.discord.ticket.database.util.schemedName

object DeadlineNotifyTable : AuditableLongIdTable(schemedName("ticket_deadline_notify")) {
    val userId = long("user_id").uniqueIndex()
    val enabled = bool("enabled").default(false)
}