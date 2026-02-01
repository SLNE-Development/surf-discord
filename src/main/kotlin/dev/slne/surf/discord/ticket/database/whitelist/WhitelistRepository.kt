package dev.slne.surf.discord.ticket.database.whitelist

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class WhitelistRepository {
    suspend fun isWhitelisted(discordId: Long) = suspendTransaction {
        WhitelistTable.selectAll().where(WhitelistTable.discordUserId eq discordId).count() > 0
    }

    suspend fun isWhitelisted(minecraftUuid: UUID) = suspendTransaction {
        WhitelistTable.selectAll().where(WhitelistTable.minecraftUuid eq minecraftUuid)
            .count() > 0
    }

    suspend fun whitelist(discordId: Long, minecraftUuid: UUID): WhitelistEntry =
        suspendTransaction {
            val entry = WhitelistEntry(
                discordId = discordId,
                minecraftUuid = minecraftUuid,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            WhitelistTable.insert {
                it[this.discordUserId] = discordId
                it[this.minecraftUuid] = minecraftUuid
                it[this.createdAt] = entry.createdAt
                it[this.updatedAt] = entry.updatedAt
            }

            entry
        }

    suspend fun blockWhitelist(discordId: Long) = suspendTransaction {
        WhitelistTable.update(where = { WhitelistTable.discordUserId eq discordId }) {
            it[this.blocked] = true
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }

    suspend fun unblockWhitelist(discordId: Long) = suspendTransaction {
        WhitelistTable.update(where = { WhitelistTable.discordUserId eq discordId }) {
            it[this.blocked] = false
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }

    suspend fun getWhitelist(discordId: Long) = suspendTransaction {
        WhitelistTable.selectAll().where(WhitelistTable.discordUserId eq discordId).map {
            WhitelistEntry(
                discordId = it[WhitelistTable.discordUserId],
                minecraftUuid = it[WhitelistTable.minecraftUuid],
                blocked = it[WhitelistTable.blocked],
                createdAt = it[WhitelistTable.createdAt],
                updatedAt = it[WhitelistTable.updatedAt]
            )
        }.firstOrNull()
    }

    suspend fun editWhitelist(
        discordId: Long,
        minecraftUuid: UUID,
        blocked: Boolean
    ) = suspendTransaction {
        WhitelistTable.update(where = { WhitelistTable.discordUserId eq discordId }) {
            it[this.minecraftUuid] = minecraftUuid
            it[this.blocked] = blocked
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }
}