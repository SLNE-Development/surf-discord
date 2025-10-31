package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.insert
import org.springframework.stereotype.Repository

@Repository
class TicketMessageRepository {
    suspend fun logMessage(ticket: Ticket, message: Message) {
        TicketMessagesTable.insert {
            it[ticketId] = ticket.ticketId
            it[authorId] = message.author.idLong
            it[authorName] = message.author.name
            it[authorAvatarUrl] = message.author.avatarUrl ?: ""
            it[content] = message.contentRaw
            it[messageId] = message.idLong
            it[referenceMessageId] = message.referencedMessage?.idLong
            it[botMessage] = message.author.isBot
            it[messageSentAt] = message.timeCreated.toInstant().toEpochMilli()
            it[messageEditedAt] = message.timeEdited?.toInstant()?.toEpochMilli()
            it[messageDeletedAt] = null
        }
    }
}