package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import dev.slne.surf.discord.ticket.database.util.AuditableLongIdTable
import dev.slne.surf.discord.ticket.database.util.schemedName

object ReplyDeadlineTable : AuditableLongIdTable(schemedName("ticket_reply_deadlines")) {
    val ticketId = nativeUuid("ticket_id")
    val threadId = long("thread_id")

    val targetUserId = long("target_user_id")
    val targetUserName = varchar("target_user_name", 64)

    val setById = long("set_by_id")
    val setByName = varchar("set_by_name", 64)

    val deadline = offsetDateTime("deadline")
}