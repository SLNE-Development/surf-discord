package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.TicketType
import org.jetbrains.exposed.dao.id.ULongIdTable

object TicketTable : ULongIdTable("discord_tickets") {
    val ticketId = uuid("ticket_id").uniqueIndex()
    val authorId = long("author_id")
    val authorName = varchar("author_name", 100)
    val authorAvatarUrl = varchar("author_avatar_url", 255).nullable()
    val guildId = long("guild_id")
    val threadId = long("thread_id").uniqueIndex()
    val ticketType = enumeration<TicketType>("ticket_type")
    val createdAt = long("created_at")
    val closedAt = long("closed_at").nullable()
    val closedById = long("closed_by").nullable()
    val closedByName = varchar("closed_by_name", 100).nullable()
    val closedByAvatarUrl = varchar("closed_by_avatar_url", 255).nullable()
    val closedReason = largeText("closed_reason").nullable()
}