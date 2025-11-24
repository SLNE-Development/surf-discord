package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketMessagesTable : LongIdTable("ticket_messages") {
    val ticketId = ulong("ticket_id")
    val authorId = varchar("author_id", 20).transform({ it.toLong() }, { it.toString() })
    val authorName = varchar("author_name", 32)
    val authorAvatarUrl = varchar("author_avatar_url", 255).nullable()

    val content = largeText("json_content")

    val referenceMessageId = varchar("references_message_id", 20).nullable()
        .transform({ it?.toLong() }, { it.toString() })
    val messageId =
        varchar("message_id", 20).uniqueIndex().transform({ it.toLong() }, { it.toString() })

    val botMessage = bool("bot_message").default(false)
    val messageSentAt = zonedDateTime("message_created_at")
    val messageEditedAt = zonedDateTime("message_edited_at").nullable()
    val messageDeletedAt = zonedDateTime("message_deleted_at").nullable()

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}
