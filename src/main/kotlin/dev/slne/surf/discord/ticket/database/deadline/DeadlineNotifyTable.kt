package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.discord.ticket.database.column.zonedDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object DeadlineNotifyTable : LongIdTable("ticket_deadline_notify") {
    val userId = varchar("user_id", 20).transform({ it.toLong() }, { it.toString() })
    val enabled = bool("enabled").default(false)

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")

    init {
        uniqueIndex(userId)
    }
}