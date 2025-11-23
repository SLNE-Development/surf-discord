package dev.slne.surf.discord.ticket.database.members

import dev.slne.surf.discord.ticket.database.ticket.TicketTable
import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable

object TicketMemberTable : LongIdTable("discord_ticket_members") {
    val ticketId = uuid("ticket_id").references(TicketTable.ticketId)
    val memberId = long("member_id")
    val memberName = varchar("member_name", 100)
    val memberAvatarUrl = varchar("member_avatar_url", 200).nullable()
    val addedAt = zonedDateTime("added_at")
    val addedById = long("added_by_id")
    val addedByName = varchar("added_by_name", 100)
    val addedByAvatarUrl = varchar("added_by_avatar_url", 200).nullable()
    val removedAt = zonedDateTime("removed_at").nullable()
    val removedById = long("removed_by_id").nullable()
    val removedByName = varchar("removed_by_name", 100).nullable()
    val removedByAvatarUrl = varchar("removed_by_avatar_url", 200).nullable()
    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")

    init {
        index(true, ticketId, memberId)
    }
}