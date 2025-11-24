package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.ticket.TicketType
import dev.slne.surf.discord.util.zonedDateTime
import org.jetbrains.exposed.dao.id.ULongIdTable
import java.util.*

object TicketTable : ULongIdTable("ticket_tickets") {
    val ticketId = varchar("ticket_id", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val authorId =
        varchar("ticket_author_id", 20).transform({ it.toLong() }, { it.toString() })
    val authorName = varchar("ticket_author_name", 64)
    val authorAvatarUrl = varchar("ticket_author_avatar_url", 255).nullable()

    val guildId = varchar("guild_id", 20).transform({ it.toLong() }, { it.toString() })
    val threadId =
        varchar("thread_id", 20).nullable().transform({ it?.toLong() }, { it.toString() })

    val ticketType = varchar("ticket_type", 255).transform({ TicketType.valueOf(it) }, { it.name })

    val openedAt = zonedDateTime("opened_at")

    val closedById =
        varchar("closed_by_id", 20).nullable().transform({ it?.toLong() }, { it.toString() })
    val closedByName = varchar("closed_by_name", 64).nullable()
    val closedByAvatarUrl = varchar("closed_by_avatar_url", 255).nullable()

    val closedReason = text("closed_reason").nullable()
    val closedAt = zonedDateTime("closed_at").nullable()

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}
