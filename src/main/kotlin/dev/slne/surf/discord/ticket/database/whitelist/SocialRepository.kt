package dev.slne.surf.discord.ticket.database.whitelist

import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import kotlinx.coroutines.flow.*
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

/**
 * Reads account links from the website owned [WebAccountsTable] and manages the freebuild
 * whitelist entries derived from them. Never writes to [WebAccountsTable] - linking a minecraft
 * account to a discord account happens on the website.
 *
 * [WebAccountsTable.userId] is a text column while [FreebuildWhitelistTable.userId] is a native
 * uuid, so the two are never joined in SQL - the web user id is resolved first and then passed as
 * a parameter.
 */
@Repository
class SocialRepository {
    private val discordAccounts = WebAccountsTable.alias("discord_accounts")
    private val minecraftAccounts = WebAccountsTable.alias("minecraft_accounts")

    private val webUserIdColumn = discordAccounts[WebAccountsTable.userId]
    private val discordIdColumn = discordAccounts[WebAccountsTable.providerAccountId]
    private val minecraftUuidColumn = minecraftAccounts[WebAccountsTable.providerAccountId]

    /** Both provider rows of the same web user joined together - one row per completed link. */
    private val linkedAccounts = discordAccounts.join(
        minecraftAccounts,
        JoinType.INNER,
        onColumn = webUserIdColumn,
        otherColumn = minecraftAccounts[WebAccountsTable.userId],
        additionalConstraint = {
            (discordAccounts[WebAccountsTable.provider] eq WebAccountProviders.DISCORD) and
                    (minecraftAccounts[WebAccountsTable.provider] eq WebAccountProviders.MINECRAFT)
        }
    )

    suspend fun findLinkByDiscordId(discordId: Long): AccountLink? = suspendTransaction {
        findLink(byDiscordId(discordId))
    }

    suspend fun isWhitelisted(discordId: Long) = suspendTransaction {
        isWhitelisted(byDiscordId(discordId))
    }

    suspend fun isWhitelisted(minecraftUuid: UUID) = suspendTransaction {
        isWhitelisted(byMinecraftUuid(minecraftUuid))
    }

    /** Creates or unblocks the whitelist entry for an already linked pair of accounts. */
    suspend fun whitelist(link: AccountLink): SocialEntry = suspendTransaction {
        val now = OffsetDateTime.now()

        if (findBlocked(link.webUserId) == null) {
            FreebuildWhitelistTable.insert {
                it[this.userId] = link.webUserId
                it[this.blocked] = false
                it[this.createdAt] = now
                it[this.updatedAt] = now
            }
        } else {
            FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.userId eq link.webUserId }) {
                it[this.blocked] = false
                it[this.updatedAt] = now
            }
        }

        link.toSocialEntry(blocked = false)
    }

    suspend fun blockWhitelist(discordId: Long) = editBlocked(discordId, true)

    suspend fun unblockWhitelist(discordId: Long) = editBlocked(discordId, false)

    suspend fun getWhitelist(discordId: Long) = suspendTransaction {
        getWhitelist(byDiscordId(discordId))
    }

    suspend fun getWhitelist(minecraftUuid: UUID) = suspendTransaction {
        getWhitelist(byMinecraftUuid(minecraftUuid))
    }

    private suspend fun editBlocked(
        discordId: Long,
        blocked: Boolean
    ) = suspendTransaction {
        val webUserId = findWebUserId(WebAccountProviders.DISCORD, discordId.toString())
            ?: return@suspendTransaction false

        FreebuildWhitelistTable.update(where = { FreebuildWhitelistTable.userId eq webUserId }) {
            it[this.blocked] = blocked
            it[this.updatedAt] = OffsetDateTime.now()
        } > 0
    }

    suspend fun findAllUuidsByDiscordIds(discordIds: LongSet): Object2LongMap<UUID> =
        suspendTransaction {
            linkedAccounts.select(webUserIdColumn, discordIdColumn, minecraftUuidColumn)
                .where(discordIdColumn inList discordIds.map(Long::toString))
                .mapNotNull { it.toAccountLink() }
                .toObject2LongMap()
        }

    suspend fun findAllDiscordIdsByUuids(uuids: Collection<UUID>): Object2LongMap<UUID> =
        suspendTransaction {
            linkedAccounts.select(webUserIdColumn, discordIdColumn, minecraftUuidColumn)
                .where(minecraftUuidColumn inList uuids.map(UUID::toString))
                .mapNotNull { it.toAccountLink() }
                .toObject2LongMap()
        }

    private fun byDiscordId(discordId: Long) = discordIdColumn eq discordId.toString()

    private fun byMinecraftUuid(minecraftUuid: UUID) = minecraftUuidColumn eq minecraftUuid.toString()

    private suspend fun findLink(constraint: Op<Boolean>): AccountLink? =
        linkedAccounts.selectAll()
            .where(constraint)
            .mapNotNull { it.toAccountLink() }
            .firstOrNull()

    private suspend fun isWhitelisted(constraint: Op<Boolean>): Boolean {
        val link = findLink(constraint) ?: return false

        return findBlocked(link.webUserId)?.not() ?: false
    }

    private suspend fun getWhitelist(constraint: Op<Boolean>): SocialEntry? {
        val link = findLink(constraint) ?: return null
        val blocked = findBlocked(link.webUserId) ?: return null

        return link.toSocialEntry(blocked)
    }

    /** The blocked flag of the whitelist entry, or `null` if the web user has no entry. */
    private suspend fun findBlocked(webUserId: UUID): Boolean? =
        FreebuildWhitelistTable.select(FreebuildWhitelistTable.blocked)
            .where(FreebuildWhitelistTable.userId eq webUserId)
            .map { it[FreebuildWhitelistTable.blocked] }
            .firstOrNull()

    private suspend fun findWebUserId(provider: String, providerAccountId: String): UUID? =
        WebAccountsTable.select(WebAccountsTable.userId)
            .where(
                (WebAccountsTable.provider eq provider) and
                        (WebAccountsTable.providerAccountId eq providerAccountId)
            )
            .map { it[WebAccountsTable.userId] }
            .firstOrNull()

    private suspend fun Flow<AccountLink>.toObject2LongMap(): Object2LongMap<UUID> {
        val links = toList()

        return Object2LongOpenHashMap<UUID>(links.size).apply {
            links.forEach { put(it.minecraftUuid, it.discordId) }
        }
    }

    private fun AccountLink.toSocialEntry(blocked: Boolean) = SocialEntry(
        webUserId = webUserId,
        discordId = discordId,
        minecraftUuid = minecraftUuid,
        blocked = blocked
    )

    private fun ResultRow.toAccountLink(): AccountLink? {
        val discordId = this[discordIdColumn].toLongOrNull() ?: return null
        val minecraftUuid = runCatching { UUID.fromString(this[minecraftUuidColumn]) }.getOrNull()
            ?: return null

        return AccountLink(
            webUserId = this[webUserIdColumn],
            discordId = discordId,
            minecraftUuid = minecraftUuid
        )
    }
}
