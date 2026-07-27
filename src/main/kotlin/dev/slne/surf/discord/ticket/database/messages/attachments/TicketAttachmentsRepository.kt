package dev.slne.surf.discord.ticket.database.messages.attachments

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.ZonedDateTime

object TicketAttachmentsRepository {
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
    ) = suspendTransaction {
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
    ) = suspendTransaction {
        TicketAttachmentsTable.deleteWhere {
            TicketAttachmentsTable.messageId eq messageId
        }
    }
}