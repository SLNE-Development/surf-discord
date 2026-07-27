package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.database.columns.time.zonedDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.ticket.database.util.schemedName

object TicketMessagesTable : LongIdTable(schemedName("ticket_messages")) {
    val ticketId = ulong("ticket_id").references(TicketTable.id)
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
