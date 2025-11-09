package dev.slne.surf.discord.ticket.database.messages.attachments

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketAttachmentsTable : LongIdTable("discord_ticket_attachments") {
    val ticketUid = uuid("ticket_uid").references(TicketTable.ticketUid).uniqueIndex()
    val messageId = long("message_id").uniqueIndex()
    val attachmentId = long("attachment_id").uniqueIndex()

    val url = varchar("url", 1024)
    val proxyUrl = varchar("proxy_url", 1024)
    val deletedAt = long("deleted_at").nullable()
}