package dev.slne.surf.discord.ticket.database.deadline

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

data class ReplyDeadline(
    val id: Long,
    val ticketId: UUID,
    val threadId: Long,
    val targetUserId: Long,
    val targetUserName: String,
    val setById: Long,
    val setByName: String,
    val deadline: ZonedDateTime,
)

@Repository
class ReplyDeadlineRepository {

    suspend fun create(
        ticketId: UUID,
        threadId: Long,
        targetUserId: Long,
        targetUserName: String,
        setById: Long,
        setByName: String,
        deadline: ZonedDateTime,
    ) = suspendTransaction {
        ReplyDeadlineTable.insert {
            it[ReplyDeadlineTable.ticketId] = ticketId
            it[ReplyDeadlineTable.threadId] = threadId
            it[ReplyDeadlineTable.targetUserId] = targetUserId
            it[ReplyDeadlineTable.targetUserName] = targetUserName
            it[ReplyDeadlineTable.setById] = setById
            it[ReplyDeadlineTable.setByName] = setByName
            it[ReplyDeadlineTable.deadline] = deadline
            it[createdAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun deleteForUserInThread(threadId: Long, targetUserId: Long) = suspendTransaction {
        ReplyDeadlineTable.deleteWhere {
            (ReplyDeadlineTable.threadId eq threadId) and
                    (ReplyDeadlineTable.targetUserId eq targetUserId)
        }
    }

    suspend fun findExpired(now: ZonedDateTime): List<ReplyDeadline> = suspendTransaction {
        ReplyDeadlineTable.selectAll()
            .where(ReplyDeadlineTable.deadline lessEq now)
            .map { it.toReplyDeadline() }
            .toList()
    }

    suspend fun delete(id: Long) = suspendTransaction {
        ReplyDeadlineTable.deleteWhere { ReplyDeadlineTable.id eq id }
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
