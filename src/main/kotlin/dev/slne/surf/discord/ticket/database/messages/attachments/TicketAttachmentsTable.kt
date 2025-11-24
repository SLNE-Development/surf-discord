package dev.slne.surf.discord.ticket.database.messages.attachments

import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketAttachmentsTable : LongIdTable("ticket_message_attachments") {
    val attachmentId =
        varchar("attachment_id", 20).uniqueIndex().transform({ it.toLong() }, { it.toString() })

    val fileName = largeText("filename")
    val url = largeText("url")
    val proxyUrl = largeText("proxy_url")
    val waveform = largeText("waveform").nullable()

    val contentType = varchar("content_type", 255).nullable()
    val description = varchar("description", 1024).nullable()

    val size = integer("size")
    val height = integer("height").nullable()
    val width = integer("width").nullable()

    val ephemeral = bool("ephemeral").default(false)
    val durationSeconds = float("duration_secs").nullable()

    val messageId = long("message_id")

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}
