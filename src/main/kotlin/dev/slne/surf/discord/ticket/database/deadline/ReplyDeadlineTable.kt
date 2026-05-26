package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.discord.ticket.database.column.zonedDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import java.util.*

object ReplyDeadlineTable : LongIdTable("ticket_reply_deadlines") {
    val ticketId = varchar("ticket_id", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val threadId = varchar("thread_id", 20).transform({ it.toLong() }, { it.toString() })

    val targetUserId = varchar("target_user_id", 20).transform({ it.toLong() }, { it.toString() })
    val targetUserName = varchar("target_user_name", 64)

    val setById = varchar("set_by_id", 20).transform({ it.toLong() }, { it.toString() })
    val setByName = varchar("set_by_name", 64)

    val deadline = zonedDateTime("deadline")

    val createdAt = zonedDateTime("created_at")
    val updatedAt = zonedDateTime("updated_at")
}