package dev.slne.surf.discord.ticket.database.messages.attachments

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class TicketAttachmentsRepository {
    suspend fun addAttachment(
        ticketId: Long,
        messageId: Long,
        attachmentId: Long,
        url: String,
        proxyUrl: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketAttachmentsTable.insert {
            it[this.ticketId] = ticketId
            it[this.messageId] = messageId
            it[this.attachmentId] = attachmentId
            it[this.url] = url
            it[this.proxyUrl] = proxyUrl
            it[this.deletedAt] = null
        }
    }

    suspend fun markDeleted(
        ticketId: Long,
        messageId: Long
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketAttachmentsTable.update({ TicketAttachmentsTable.messageId eq messageId }) {
            it[this.deletedAt] = System.currentTimeMillis()
        }
    }
}