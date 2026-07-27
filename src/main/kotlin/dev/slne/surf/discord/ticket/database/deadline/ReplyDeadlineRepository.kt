package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.lessEq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlinx.coroutines.flow.toList
import java.time.OffsetDateTime
import java.util.*

data class ReplyDeadline(
    val id: ULong,
    val ticketId: UUID,
    val threadId: Long,
    val targetUserId: Long,
    val targetUserName: String,
    val setById: Long,
    val setByName: String,
    val deadline: OffsetDateTime,
)

object ReplyDeadlineRepository {
    suspend fun create(
        ticketId: UUID,
        threadId: Long,
        targetUserId: Long,
        targetUserName: String,
        setById: Long,
        setByName: String,
        deadline: OffsetDateTime,
    ) = suspendTransaction {
        ReplyDeadlineTable.insert {
            it[ReplyDeadlineTable.ticketId] = ticketId
            it[ReplyDeadlineTable.threadId] = threadId
            it[ReplyDeadlineTable.targetUserId] = targetUserId
            it[ReplyDeadlineTable.targetUserName] = targetUserName
            it[ReplyDeadlineTable.setById] = setById
            it[ReplyDeadlineTable.setByName] = setByName
            it[ReplyDeadlineTable.deadline] = deadline
        }
    }

    suspend fun deleteForUserInThread(threadId: Long, targetUserId: Long) = suspendTransaction {
        ReplyDeadlineTable.deleteWhere {
            (ReplyDeadlineTable.threadId eq threadId) and
                    (ReplyDeadlineTable.targetUserId eq targetUserId)
        }
    }

    suspend fun findExpired(now: OffsetDateTime): List<ReplyDeadline> = suspendTransaction {
        ReplyDeadlineTable.selectAll()
            .where(ReplyDeadlineTable.deadline lessEq now)
            .toList()
            .map { it.toReplyDeadline() }
    }

    suspend fun delete(deadlineId: ULong): Boolean = suspendTransaction {
        val deletedCount = ReplyDeadlineTable.deleteWhere { ReplyDeadlineTable.id eq deadlineId }
        deletedCount > 0
    }

    private fun ResultRow.toReplyDeadline() = ReplyDeadline(
        id = this[ReplyDeadlineTable.id].value,
        ticketId = this[ReplyDeadlineTable.ticketId],
        threadId = this[ReplyDeadlineTable.threadId],
        targetUserId = this[ReplyDeadlineTable.targetUserId],
        targetUserName = this[ReplyDeadlineTable.targetUserName],
        setById = this[ReplyDeadlineTable.setById],
        setByName = this[ReplyDeadlineTable.setByName],
        deadline = this[ReplyDeadlineTable.deadline],
    )
}
