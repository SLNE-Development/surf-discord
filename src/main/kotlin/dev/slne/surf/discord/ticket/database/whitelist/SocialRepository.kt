package dev.slne.surf.discord.ticket.database.whitelist

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class SocialRepository {
    suspend fun isWhitelisted(discordId: Long) = suspendTransaction {
        SocialsTable.selectAll().where(SocialsTable.discordUserId eq discordId).count() > 0
    }

    suspend fun isWhitelisted(minecraftUuid: UUID) = suspendTransaction {
        SocialsTable.selectAll().where(SocialsTable.minecraftUuid eq minecraftUuid)
            .count() > 0
    }

    suspend fun whitelist(discordId: Long, minecraftUuid: UUID): SocialEntry =
        suspendTransaction {
            val entry = SocialEntry(
                discordId = discordId,
                minecraftUuid = minecraftUuid,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            SocialsTable.insert {
                it[this.discordUserId] = discordId
                it[this.minecraftUuid] = minecraftUuid
                it[this.createdAt] = entry.createdAt
                it[this.updatedAt] = entry.updatedAt
            }

            entry
        }

    suspend fun blockWhitelist(discordId: Long) = suspendTransaction {
        SocialsTable.update(where = { SocialsTable.discordUserId eq discordId }) {
            it[this.blocked] = true
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun unblockWhitelist(discordId: Long) = suspendTransaction {
        SocialsTable.update(where = { SocialsTable.discordUserId eq discordId }) {
            it[this.blocked] = false
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun getWhitelist(discordId: Long) = suspendTransaction {
        SocialsTable.selectAll().where(SocialsTable.discordUserId eq discordId).map {
            SocialEntry(
                discordId = it[SocialsTable.discordUserId],
                minecraftUuid = it[SocialsTable.minecraftUuid],
                blocked = it[SocialsTable.blocked],
                createdAt = it[SocialsTable.createdAt],
                updatedAt = it[SocialsTable.updatedAt]
            )
        }.firstOrNull()
    }

    suspend fun getWhitelist(minecraftUuid: UUID) = suspendTransaction {
        SocialsTable.selectAll().where(SocialsTable.minecraftUuid eq minecraftUuid).map {
            SocialEntry(
                discordId = it[SocialsTable.discordUserId],
                minecraftUuid = it[SocialsTable.minecraftUuid],
                blocked = it[SocialsTable.blocked],
                createdAt = it[SocialsTable.createdAt],
                updatedAt = it[SocialsTable.updatedAt]
            )
        }.firstOrNull()
    }

    suspend fun editWhitelist(
        discordId: Long,
        minecraftUuid: UUID,
        blocked: Boolean
    ) = suspendTransaction {
        SocialsTable.update(where = { SocialsTable.discordUserId eq discordId }) {
            it[this.minecraftUuid] = minecraftUuid
            it[this.blocked] = blocked
            it[this.updatedAt] = OffsetDateTime.now()
        }
    }

    suspend fun deleteWhitelist(discordId: Long) = suspendTransaction {
        SocialsTable.deleteWhere { SocialsTable.discordUserId eq discordId } > 0
    }
}