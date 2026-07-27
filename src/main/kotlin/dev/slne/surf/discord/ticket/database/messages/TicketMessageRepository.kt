package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.update
import dev.slne.surf.discord.ticket.Ticket
import kotlinx.coroutines.flow.singleOrNull
import net.dv8tion.jda.api.entities.Message
import java.time.ZonedDateTime

object TicketMessageRepository {
    suspend fun logMessage(ticket: Ticket, message: Message): Long =
        suspendTransaction {
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
        suspendTransaction {
            TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
                it[messageEditedAt] = ZonedDateTime.now()
                it[updatedAt] = ZonedDateTime.now()
                it[TicketMessagesTable.content] = content
            }
        }

    suspend fun logMessageDeleted(messageId: Long) = suspendTransaction {
        TicketMessagesTable.update({ TicketMessagesTable.messageId eq messageId }) {
            it[messageDeletedAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun getDbIdFromDiscordMessageId(discordMessageId: Long): Long? =
        suspendTransaction {
            TicketMessagesTable
                .select(TicketMessagesTable.id)
                .where { TicketMessagesTable.messageId eq discordMessageId }
                .singleOrNull()
                ?.get(TicketMessagesTable.id)
                ?.value
        }
}