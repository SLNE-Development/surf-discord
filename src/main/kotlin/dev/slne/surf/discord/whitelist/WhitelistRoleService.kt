package dev.slne.surf.discord.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.database.whitelist.SocialRepository
import it.unimi.dsi.fastutil.longs.LongSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

object WhitelistRoleService {
    private const val MAX_CONCURRENT_ROLE_UPDATES = 16

    suspend fun syncWhitelistRole() {
        val whitelistedDiscordIds = SocialRepository.findAllWhitelistedDiscordIds()
        val roleId = botConfig.roles.whitelistedRoleId
        var guildsWithRole = 0

        for (guild in jda.guilds) {
            val role = guild.getRoleById(roleId) ?: continue
            guildsWithRole++

            try {
                syncGuild(guild, role, whitelistedDiscordIds)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                logger.warn(
                    "Failed to synchronize the whitelisted role in guild ${guild.id}",
                    exception
                )
            }
        }

        if (guildsWithRole == 0) {
            logger.warn("Whitelisted role $roleId does not exist in any guild the bot is in")
        }
    }

    private suspend fun syncGuild(
        guild: Guild,
        role: Role,
        whitelistedDiscordIds: LongSet
    ) {
        val relevantMembers = guild.findMembers { member ->
            whitelistedDiscordIds.contains(member.idLong) || role in member.roles
        }.await()
        val membersById = relevantMembers.associateBy(Member::getIdLong)

        val changes = calculateWhitelistRoleChanges(
            currentRoleHolderIds = relevantMembers
                .asSequence()
                .filter { role in it.roles }
                .map(Member::getIdLong)
                .toSet(),
            whitelistedMemberIds = relevantMembers
                .asSequence()
                .filter { whitelistedDiscordIds.contains(it.idLong) }
                .map(Member::getIdLong)
                .toSet()
        )

        val semaphore = Semaphore(MAX_CONCURRENT_ROLE_UPDATES)
        supervisorScope {
            for (discordId in changes.toAdd) {
                launch {
                    semaphore.withPermit {
                        updateRole(guild, membersById.getValue(discordId), role, add = true)
                    }
                }
            }

            for (discordId in changes.toRemove) {
                launch {
                    semaphore.withPermit {
                        updateRole(guild, membersById.getValue(discordId), role, add = false)
                    }
                }
            }
        }
    }

    private suspend fun updateRole(guild: Guild, member: Member, role: Role, add: Boolean) {
        try {
            if (add) {
                guild.addRoleToMember(member, role).reason("Whitelisted").await()
                logger.info("Added the whitelisted role to user ${member.id} in guild ${guild.id}")
            } else {
                guild.removeRoleFromMember(member, role).reason("No longer whitelisted").await()
                logger.info("Removed the whitelisted role from user ${member.id} in guild ${guild.id}")
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            logger.warn(
                "Failed to ${if (add) "add" else "remove"} the whitelisted role for user ${member.id} in guild ${guild.id}",
                exception
            )
        }
    }
}
