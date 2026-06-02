package dev.slne.surf.discord.ticket.database.deadline

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class DeadlineNotifyRepository {

    suspend fun setEnabled(userId: Long, enabled: Boolean) = suspendTransaction {
        DeadlineNotifyTable.upsert(
            onUpdateExclude = listOf(DeadlineNotifyTable.createdAt),
        ) {
            it[DeadlineNotifyTable.userId] = userId
            it[DeadlineNotifyTable.enabled] = enabled
            it[createdAt] = ZonedDateTime.now()
            it[updatedAt] = ZonedDateTime.now()
        }
    }

    suspend fun isEnabled(userId: Long): Boolean = suspendTransaction {
        DeadlineNotifyTable.selectAll()
            .where(DeadlineNotifyTable.userId eq userId)
            .map { it[DeadlineNotifyTable.enabled] }
            .firstOrNull() ?: false
    }
}