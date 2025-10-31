package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class TicketMessageRepository {
    suspend fun logMessage(ticket: Ticket, message: Message) =
        newSuspendedTransaction(Dispatchers.IO) {
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
                it[messageEditedAt] = null
                it[messageDeletedAt] = null
            }
        }

    suspend fun logMessageEdited(messageId: Long, content: String) =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
                it[messageEditedAt] = System.currentTimeMillis()
                it[TicketMessagesTable.content] = content
            }
        }

    suspend fun logMessageDeleted(messageId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
            it[messageDeletedAt] = System.currentTimeMillis()
        }
    }
}