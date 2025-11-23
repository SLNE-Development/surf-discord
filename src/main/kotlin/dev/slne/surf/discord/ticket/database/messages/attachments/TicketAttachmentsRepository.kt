package dev.slne.surf.discord.ticket.database.messages.attachments

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class TicketAttachmentsRepository {
    suspend fun addAttachment(
        attachmentId: Long,
        fileName: String,
        url: String,
        proxyUrl: String,
        waveform: String?,
        contentType: String?,
        description: String?,
        size: Int,
        height: Int?,
        width: Int?,
        ephemeral: Boolean,
        durationSeconds: Float?,
        messageId: Long,
    ) = newSuspendedTransaction(Dispatchers.IO) {
        TicketAttachmentsTable.insert {
            it[this.attachmentId] = attachmentId
            it[this.fileName] = fileName
            it[this.url] = url
            it[this.proxyUrl] = proxyUrl
            it[this.waveform] = waveform
            it[this.contentType] = contentType
            it[this.description] = description
            it[this.size] = size
            it[this.height] = height
            it[this.width] = width
            it[this.ephemeral] = ephemeral
            it[this.durationSeconds] = durationSeconds
            it[this.messageId] = messageId
            it[this.createdAt] = ZonedDateTime.now()
            it[this.updatedAt] = ZonedDateTime.now()
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