package dev.slne.surf.discord.ticket.database.messages.attachments

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class TicketAttachmentsRepository {
    suspend fun addAttachment(
        ticketUid: UUID,
        messageId: Long,
        attachmentId: Long,
        url: String,
        proxyUrl: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketAttachmentsTable.insert {
            it[this.ticketUid] = ticketUid
            it[this.messageId] = messageId
            it[this.attachmentId] = attachmentId
            it[this.url] = url
            it[this.proxyUrl] = proxyUrl
            it[this.deletedAt] = null
        }
    }

    suspend fun delete(
        messageId: Long
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketAttachmentsTable.deleteWhere {
            TicketAttachmentsTable.messageId eq messageId
        }
    }
}