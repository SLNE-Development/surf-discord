package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketMessagesTable : LongIdTable("ticket_messages") {
    val ticketId = reference("ticket_id", TicketTable)
    val authorId = long("author_id")
    val authorName = varchar("author_name", 100)
    val authorAvatarUrl = varchar("author_avatar_url", 255)
    val content = largeText("content")
    val messageId = long("message_id").uniqueIndex()
    val referenceMessageId = long("reference_message_id").nullable()
    val botMessage = bool("bot_message").default(false)

    val messageSentAt = long("message_created_at")
    val messageEditedAt = long("message_edited_at").nullable()
    val messageDeletedAt = long("message_deleted_at").nullable()
}