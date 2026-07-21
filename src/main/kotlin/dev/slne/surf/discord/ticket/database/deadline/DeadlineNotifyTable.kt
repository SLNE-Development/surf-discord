package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.database.table.AuditableLongIdTable

object DeadlineNotifyTable : AuditableLongIdTable("ticket_deadline_notify") {
    val userId = long("user_id").uniqueIndex()
    val enabled = bool("enabled").default(false)
}