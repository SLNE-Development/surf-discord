package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import kotlinx.coroutines.flow.*
import java.util.*

object SocialRepository {
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

    suspend fun shouldBeAbleToPlayIfMember(discordProviderId: Long) =
        findLinkByDiscordId(discordProviderId) != null

    suspend fun findLinkByDiscordId(discordId: Long): AccountLink? = suspendTransaction {
        findLink(byDiscordId(discordId))
    }

    suspend fun findLinkByMinecraftName(minecraftName: String): AccountLink? {
        val minecraftUuid = PlayerLookupService.getUuid(minecraftName) ?: return null
        return findLink(byMinecraftUuid(minecraftUuid))
    }

    suspend fun hasWebUser(discordProviderId: Long) = suspendTransaction {
        findWebUserId(WebAccountProviders.DISCORD, discordProviderId.toString()) != null
    }

    suspend fun findAllWhitelistedDiscordIds(): LongSet = suspendTransaction {
        val discordIds = LongOpenHashSet()

        linkedAccounts.select(discordIdColumn)
            .mapNotNull { it[discordIdColumn].toLongOrNull() }
            .collect(discordIds::add)

        discordIds
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

    private fun byMinecraftUuid(minecraftUuid: UUID) =
        minecraftUuidColumn eq minecraftUuid.toString()

    private suspend fun findLink(constraint: Op<Boolean>): AccountLink? =
        linkedAccounts.selectAll()
            .where(constraint)
            .mapNotNull { it.toAccountLink() }
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
