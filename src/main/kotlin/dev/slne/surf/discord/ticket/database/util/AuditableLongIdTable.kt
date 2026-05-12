package dev.slne.surf.discord.ticket.database.util

import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

open class AuditableLongIdTable(name: String) : LongIdTable(name) {
    val createdAt = offsetDateTime("created_at")
    val updatedAt = offsetDateTime("updated_at")
}