package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import java.time.OffsetDateTime

/**
 * Migration helper to convert rows from the legacy `social_connections` table
 * into the new [SocialConnectionsTable] and [FreebuildWhitelistTable].
 *
 * The migration is idempotent – already-converted rows are skipped.
 */
object SocialsMigration {
    private object LegacySocialsTable : Table("social_connections") {
        val discordUserId = long("discord_user_id")
        val minecraftUuid = nativeUuid("minecraft_uuid").nullable()
        val twitchId = long("twitch_id").nullable()
        val blocked = bool("blocked").default(false)
        val createdAt = offsetDateTime("created_at")
        val updatedAt = offsetDateTime("updated_at")
    }

    /**
     * Migrate all legacy rows. Prints a progress bar to stdout.
     */
    suspend fun migrateLegacy() = suspendTransaction {
        val rows = LegacySocialsTable.selectAll().toList()
        val total = rows.size

        for ((index, row) in rows.withIndex()) {
            val current = index + 1

            val minecraftUuid = row[LegacySocialsTable.minecraftUuid] ?: run {
                printProgress(current, total)
                continue
            }

            val discordId = row[LegacySocialsTable.discordUserId]
            val twitchId = row[LegacySocialsTable.twitchId]
            val blocked = row[LegacySocialsTable.blocked]
            val createdAt = row[LegacySocialsTable.createdAt]
            val updatedAt = row[LegacySocialsTable.updatedAt]

            val existingConnectionId = SocialConnectionsTable.selectAll()
                .where(SocialConnectionsTable.minecraftUuid eq minecraftUuid)
                .map { it[SocialConnectionsTable.id].value }
                .firstOrNull()

            if (existingConnectionId != null) {
                syncWhitelistEntry(existingConnectionId, blocked, createdAt, updatedAt)
                printProgress(current, total)
                continue
            }

            val connectionId = SocialConnectionsTable.insertReturning {
                it[this.minecraftUuid] = minecraftUuid
                it[this.discordUserId] = discordId
                twitchId?.let { id -> it[this.twitchId] = id }
                it[this.createdAt] = createdAt
                it[this.updatedAt] = updatedAt
            }.first()[SocialConnectionsTable.id].value

            FreebuildWhitelistTable.insert {
                it[this.socialConnectionId] = connectionId
                it[this.blocked] = blocked
                it[this.createdAt] = createdAt
                it[this.updatedAt] = updatedAt
            }

            printProgress(current, total)
        }
    }

    private suspend fun syncWhitelistEntry(
        connectionId: Long,
        blocked: Boolean,
        createdAt: OffsetDateTime,
        updatedAt: OffsetDateTime
    ) {
        val existingBlocked = FreebuildWhitelistTable.selectAll()
            .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull()

        if (existingBlocked == null) {
            FreebuildWhitelistTable.insert {
                it[this.socialConnectionId] = connectionId
                it[this.blocked] = blocked
                it[this.createdAt] = createdAt
                it[this.updatedAt] = updatedAt
            }
        } else if (existingBlocked != blocked) {
            FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.socialConnectionId eq connectionId }) {
                it[this.blocked] = blocked
                it[this.updatedAt] = updatedAt
            }
        }
    }

    private fun printProgress(current: Int, total: Int) {
        val percent = if (total == 0) 100 else (current * 100) / total
        val filled = (percent / 2).coerceIn(0, 50)
        val bar = "=".repeat(filled) + " ".repeat(50 - filled)
        print("\r[$bar] $percent% ($current/$total)")
    }
}
