package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.database.columns.time.zonedDateTime
import dev.slne.surf.database.table.AuditableLongIdTable
import dev.slne.surf.discord.ticket.TicketType
import java.util.*

object TicketTable : AuditableLongIdTable("ticket_tickets") {
    val ticketId = char("ticket_id", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val authorId = char("ticket_author_id", 20).transform({ it.toLong() }, { it.toString() })
    val authorName = char("ticket_author_name", 64)
    val authorAvatarUrl = varchar("ticket_author_avatar_url", 255).nullable()

    val guildId = char("guild_id", 20).transform({ it.toLong() }, { it.toString() })
    val threadId =
        char("thread_id", 20).nullable().transform({ it?.toLong() }, { it.toString() })

    val ticketType = varchar("ticket_type", 255).transform({ TicketType.valueOf(it) }, { it.name })

    val openedAt = zonedDateTime("opened_at")

    val closedById =
        char("closed_by_id", 20).nullable().transform({ it?.toLong() }, { it?.toString() })
    val closedByName = char("closed_by_name", 64).nullable()
    val closedByAvatarUrl = varchar("closed_by_avatar_url", 255).nullable()

    val closedReason = text("closed_reason").nullable()
    val closedAt = zonedDateTime("closed_at").nullable()
}
