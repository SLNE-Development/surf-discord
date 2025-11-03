package dev.slne.surf.discord.ticket.database.messages.attachments

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketAttachmentsTable : LongIdTable("discord_ticket_attachments") {
    val ticketUid = uuid("ticket_uid").references(TicketTable.ticketUid).uniqueIndex()
    val messageId = long("message_id").uniqueIndex()
    val attachmentId = long("attachment_id").uniqueIndex()

    // FIXME: 03.11.2025 17:28 not set, also this is missing a few attributes that are available via jda
    val filename = varchar("filename", 512)
    val url = varchar("url", 1024)
    val proxyUrl = varchar("proxy_url", 1024)
    val deletedAt = long("deleted_at").nullable()
}