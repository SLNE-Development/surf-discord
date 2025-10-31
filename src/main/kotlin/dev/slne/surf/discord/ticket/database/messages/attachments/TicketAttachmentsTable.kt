package dev.slne.surf.discord.ticket.database.messages.attachments

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketAttachmentsTable : LongIdTable("discord_ticket_attachments") {
    val ticketId = long("ticket_id").references(TicketTable.tickedId).uniqueIndex()
    val messageId = long("message_id").uniqueIndex()
    val attachmentId = long("attachment_id").uniqueIndex()
    val filename = varchar("filename", 512)
    val url = varchar("url", 1024)
    val proxyUrl = varchar("proxy_url", 1024)
    val deletedAt = long("deleted_at").nullable()
}