package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.ULongIdTable

object TicketTable : ULongIdTable("discord_tickets") {
    val ticketId = uuid("ticket_id").uniqueIndex()
    val authorId = long("ticket_author_id")
    val authorName = varchar("ticket_author_name", 100)
    val authorAvatarUrl = varchar("ticket_author_avatar_url", 255).nullable()
    val guildId = long("guild_id")
    val threadId = long("thread_id").uniqueIndex()
    val ticketType = enumeration<TicketType>("ticket_type")
    val openedAt = zonedDateTime("opened_at")
    val closedById = long("closed_by_id").nullable()
    val closedByName = varchar("closed_by_name", 100).nullable()
    val closedByAvatarUrl = varchar("closed_by_avatar_url", 255).nullable()
    val closedReason = largeText("closed_reason").nullable()
    val closedAt = zonedDateTime("closed_at").nullable()
    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}