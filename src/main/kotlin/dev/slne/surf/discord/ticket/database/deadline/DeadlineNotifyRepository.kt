package dev.slne.surf.discord.ticket.database.deadline

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import kotlinx.coroutines.flow.singleOrNull
import org.springframework.stereotype.Repository

@Repository
class DeadlineNotifyRepository {
    suspend fun setEnabled(userId: Long, enabled: Boolean) = suspendTransaction {
        DeadlineNotifyTable.upsert {
            it[DeadlineNotifyTable.userId] = userId
            it[DeadlineNotifyTable.enabled] = enabled
        }
    }

    suspend fun isEnabled(userId: Long): Boolean = suspendTransaction {
        DeadlineNotifyTable.select(DeadlineNotifyTable.enabled)
            .where { DeadlineNotifyTable.userId eq userId }
            .singleOrNull()
            ?.get(DeadlineNotifyTable.enabled) ?: false
    }
}