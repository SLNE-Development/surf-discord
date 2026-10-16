package dev.slne.surf.discord.ticket.database.notes

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object TicketNotesTable : AuditableLongIdTable("ticket_notes") {
    val ticketId = nativeUuid("ticket_id")
    val authorId = varchar("author_id", 255).transform({ it.toLong() }, { it.toString() })
    val authorName = varchar("author_name", 255)
    val authorAvatarUrl = varchar("author_avatar_url", 255).nullable()
    val content = largeText("content")
}