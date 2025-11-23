package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketMessagesTable : LongIdTable("discord_ticket_messages") {
    val ticketId = uuid("ticket_id").references(TicketTable.ticketId)
    val authorId = long("author_id")
    val authorName = varchar("author_name", 100)
    val authorAvatarUrl = varchar("author_avatar_url", 255)
    val content = largeText("json_content")
    val referenceMessageId = long("reference_message_id").nullable()
    val messageId = long("message_id").uniqueIndex()
    val botMessage = bool("bot_message").default(false)

    val messageSentAt = zonedDateTime("message_created_at")
    val messageEditedAt = zonedDateTime("message_edited_at").nullable()
    val messageDeletedAt = zonedDateTime("message_deleted_at").nullable()

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}