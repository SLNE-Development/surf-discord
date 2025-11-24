package dev.slne.surf.discord.ticket.database.members

import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.LongIdTable
import java.util.*

object TicketMemberTable : LongIdTable("ticket_members") {
    val ticketId = varchar("ticket_id", 50).transform({ UUID.fromString(it) }, { it.toString() })
    val memberId = varchar("member_id", 255).transform({ it.toLong() }, { it.toString() })
    val memberName = varchar("member_name", 255)
    val memberAvatarUrl = varchar("member_avatar_url", 255).nullable()
    val addedAt = zonedDateTime("added_at")
    val addedById = varchar("added_by_id", 255).transform({ it.toLong() }, { it.toString() })
    val addedByName = varchar("added_by_name", 255)
    val addedByAvatarUrl = varchar("added_by_avatar_url", 255).nullable()
    val removedAt = zonedDateTime("removed_at").nullable()
    val removedById =
        varchar("removed_by_id", 255).nullable().transform({ it?.toLong() }, { it.toString() })
    val removedByName = varchar("removed_by_name", 255).nullable()
    val removedByAvatarUrl = varchar("removed_by_avatar_url", 255).nullable()
    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")

    init {
        index(true, ticketId, memberId)
    }
}