package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.inList
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class SocialRepository {
    suspend fun isWhitelisted(discordId: Long) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull()

        if (connectionId == null) return@suspendTransaction false

        FreebuildWhitelistTable.selectAll()
            .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull()?.let { blocked -> !blocked } ?: false
    }

    suspend fun isWhitelisted(minecraftUuid: UUID) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.minecraftUuid eq minecraftUuid)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull()

        if (connectionId == null) return@suspendTransaction false

        FreebuildWhitelistTable.selectAll()
            .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull()?.let { blocked -> !blocked } ?: false
    }

    suspend fun whitelist(discordId: Long, minecraftUuid: UUID): SocialEntry =
        suspendTransaction {
            val now = OffsetDateTime.now()

            val existingConnectionId = SocialConnectionsTable.selectAll()
                .where(SocialConnectionsTable.minecraftUuid eq minecraftUuid)
                .map { it[SocialConnectionsTable.id].value }
                .firstOrNull()

            val connectionId = if (existingConnectionId == null) {
                SocialConnectionsTable.insert {
                    it[this.discordUserId] = discordId
                    it[this.minecraftUuid] = minecraftUuid
                    it[this.createdAt] = now
                    it[this.updatedAt] = now
                }

                SocialConnectionsTable.selectAll()
                    .where(SocialConnectionsTable.minecraftUuid eq minecraftUuid)
                    .map { it[SocialConnectionsTable.id].value }
                    .first()
            } else existingConnectionId

            // Ensure whitelist entry exists
            val existingWhitelist = FreebuildWhitelistTable.selectAll()
                .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
                .map { it[FreebuildWhitelistTable.blocked] }
                .firstOrNull()

            if (existingWhitelist == null) {
                FreebuildWhitelistTable.insert {
                    it[this.socialConnectionId] = connectionId
                    it[this.blocked] = false
                    it[this.createdAt] = now
                    it[this.updatedAt] = now
                }
            } else {
                FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.socialConnectionId eq connectionId }) {
                    it[this.blocked] = false
                    it[this.updatedAt] = now
                }
            }

            SocialEntry(
                discordId = discordId,
                minecraftUuid = minecraftUuid,
                blocked = false,
                createdAt = now,
                updatedAt = now
            )
        }

    suspend fun blockWhitelist(discordId: Long) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull() ?: return@suspendTransaction false

        FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.socialConnectionId eq connectionId }) {
            it[this.blocked] = true
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun unblockWhitelist(discordId: Long) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull() ?: return@suspendTransaction false

        FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.socialConnectionId eq connectionId }) {
            it[this.blocked] = false
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun getWhitelist(discordId: Long) = suspendTransaction {
        val connectionRow = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it }
            .firstOrNull() ?: return@suspendTransaction null

        val connectionId = connectionRow[SocialConnectionsTable.id].value

        val blocked = FreebuildWhitelistTable.selectAll()
            .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull() ?: false

        SocialEntry(
            discordId = connectionRow[SocialConnectionsTable.discordUserId]!!,
            minecraftUuid = connectionRow[SocialConnectionsTable.minecraftUuid],
            blocked = blocked,
            createdAt = connectionRow[SocialConnectionsTable.createdAt],
            updatedAt = connectionRow[SocialConnectionsTable.updatedAt]
        )
    }

    suspend fun getWhitelist(minecraftUuid: UUID) = suspendTransaction {
        val connectionRow = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.minecraftUuid eq minecraftUuid)
            .map { it }
            .firstOrNull() ?: return@suspendTransaction null

        val connectionId = connectionRow[SocialConnectionsTable.id].value

        val blocked = FreebuildWhitelistTable.selectAll()
            .where(FreebuildWhitelistTable.socialConnectionId eq connectionId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull() ?: false

        SocialEntry(
            discordId = connectionRow[SocialConnectionsTable.discordUserId]
                ?: return@suspendTransaction null,
            minecraftUuid = connectionRow[SocialConnectionsTable.minecraftUuid],
            blocked = blocked,
            createdAt = connectionRow[SocialConnectionsTable.createdAt],
            updatedAt = connectionRow[SocialConnectionsTable.updatedAt]
        )
    }

    suspend fun editMinecraftName(
        discordId: Long,
        minecraftUuid: UUID
    ) = suspendTransaction {
        SocialConnectionsTable.update(where = { SocialConnectionsTable.discordUserId eq discordId }) {
            it[this.minecraftUuid] = minecraftUuid
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }

    suspend fun editDiscordId(
        oldDiscordId: Long,
        newDiscordId: Long
    ) = suspendTransaction {
        SocialConnectionsTable.update(where = { SocialConnectionsTable.discordUserId eq oldDiscordId }) {
            it[this.discordUserId] = newDiscordId
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }

    suspend fun editBlocked(
        discordId: Long,
        blocked: Boolean
    ) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull() ?: return@suspendTransaction false

        FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.socialConnectionId eq connectionId }) {
            it[this.blocked] = blocked
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun deleteWhitelist(discordId: Long) = suspendTransaction {
        val connectionId = SocialConnectionsTable.selectAll()
            .where(SocialConnectionsTable.discordUserId eq discordId)
            .map { it[SocialConnectionsTable.id].value }
            .firstOrNull() ?: return@suspendTransaction false

        FreebuildWhitelistTable.deleteWhere { FreebuildWhitelistTable.socialConnectionId eq connectionId } > 0
    }

    suspend fun findAllUuidsByDiscordIds(discordIds: LongSet): Object2LongMap<UUID> =
        suspendTransaction {
            SocialConnectionsTable.select(
                SocialConnectionsTable.minecraftUuid,
                SocialConnectionsTable.discordUserId
            )
                .where(SocialConnectionsTable.discordUserId inList discordIds)
                .mapNotNull {
                    it[SocialConnectionsTable.discordUserId]?.let { value ->
                        Object2LongMap.entry(
                            it[SocialConnectionsTable.minecraftUuid],
                            value
                        )
                    }
                }
                .toList()
                .let { results ->
                    Object2LongOpenHashMap<UUID>().apply {
                        results.forEach { put(it.key, it.longValue) }
                    }
                }
        }

    suspend fun findAllDiscordIdsByUuids(uuids: Collection<UUID>): Object2LongMap<UUID> =
        suspendTransaction {
            SocialConnectionsTable.select(
                SocialConnectionsTable.minecraftUuid,
                SocialConnectionsTable.discordUserId
            )
                .where(SocialConnectionsTable.minecraftUuid inList uuids)
                .mapNotNull {
                    it[SocialConnectionsTable.discordUserId]?.let { value ->
                        Object2LongMap.entry(
                            it[SocialConnectionsTable.minecraftUuid],
                            value
                        )
                    }
                }
                .toList()
                .let { results ->
                    Object2LongOpenHashMap<UUID>().apply {
                        results.forEach { put(it.key, it.longValue) }
                    }
                }
        }
}