package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.entities.Message
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class TicketMessageRepository {
    suspend fun logMessage(ticket: Ticket, message: Message): Long =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMessagesTable.insert {
                it[ticketId] =
                    ticket.internalTicketId ?: error("Ticket ${ticket.ticketId} has no internal ID")
                it[authorId] = message.author.idLong
                it[authorName] = message.author.name
                it[authorAvatarUrl] = message.author.avatarUrl ?: ""
                it[content] = message.contentRaw
                it[messageId] = message.idLong
                it[referenceMessageId] = message.referencedMessage?.idLong
                it[botMessage] = message.author.isBot
                it[messageSentAt] = message.timeCreated.toZonedDateTime()
                it[messageEditedAt] = null
                it[messageDeletedAt] = null
                it[createdAt] = ZonedDateTime.now()
                it[updatedAt] = ZonedDateTime.now()
            }[TicketMessagesTable.id].value
        }

    suspend fun logMessageEdited(messageId: Long, content: String) =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
                it[messageEditedAt] = ZonedDateTime.now()
                it[updatedAt] = ZonedDateTime.now()
                it[TicketMessagesTable.content] = content
            }
        }

    suspend fun logMessageDeleted(messageId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
            it[messageDeletedAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun getDbIdFromDiscordMessageId(discordMessageId: Long): Long? =
        newSuspendedTransaction(Dispatchers.IO) {
            TicketMessagesTable
                .selectAll()
                .where { TicketMessagesTable.messageId eq discordMessageId }
                .map { it[TicketMessagesTable.id].value }
                .singleOrNull()
        }
}