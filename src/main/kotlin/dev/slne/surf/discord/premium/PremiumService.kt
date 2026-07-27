package dev.slne.surf.discord.premium

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.api.LuckpermsApi
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.ticket.database.whitelist.SocialRepository
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2LongMaps
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object PremiumService {
    private val logger = ComponentLogger.logger()

    suspend fun syncPremium() {
        if (!LuckpermsApi.isAvailable()) {
            logger.warn("LuckPerms API token is not set, skipping premium UUID fetch")
            return
        }

        val currentActivePremiumUuids = LuckpermsApi.findAllPremiumUuids()

        jda.guilds.forEach { guild ->
            val role = guild.getRoleById(botConfig.roles.premiumRoleId) ?: return@forEach
            val premiumMembers = guild.findMembersWithRoles(role).await()
            val premiumMemberIds = LongOpenHashSet(premiumMembers.size)
            for (member in premiumMembers) {
                premiumMemberIds.add(member.idLong)
            }

            val currentPremiumRoleUsersByUuid = if (premiumMemberIds.isEmpty()) {
                Object2LongMaps.emptyMap()
            } else {
                SocialRepository.findAllUuidsByDiscordIds(premiumMemberIds)
            }
            val currentPremiumRoleUuids = currentPremiumRoleUsersByUuid.keys

            val toRemoveUuids = currentPremiumRoleUuids - currentActivePremiumUuids
            val toAddUuids = currentActivePremiumUuids - currentPremiumRoleUuids

            val semaphore = Semaphore(16)
            supervisorScope {
                for (uuid in toRemoveUuids) {
                    launch {
                        semaphore.withPermit {
                            val discordId = currentPremiumRoleUsersByUuid.getLong(uuid)

                            try {
                                val member = runCatching {
                                    guild.retrieveMemberById(discordId).await()
                                }.getOrNull()

                                if (member != null) {
                                    guild.removeRoleFromMember(member, role)
                                        .reason("Premium expired").await()
                                }
                            } catch (e: Exception) {
                                logger.warn(
                                    "Failed to remove premium role from user {} / uuid {} in guild {}",
                                    discordId,
                                    uuid,
                                    guild.id,
                                    e
                                )
                            }
                        }
                    }
                }
            }

            val toAddUsersByUuid = if (toAddUuids.isEmpty()) {
                Object2LongMaps.emptyMap()
            } else {
                SocialRepository.findAllDiscordIdsByUuids(toAddUuids)
            }

            supervisorScope {
                for (entry in toAddUsersByUuid.object2LongEntrySet()) {
                    launch {
                        semaphore.withPermit {
                            val discordId = entry.longValue

                            try {
                                val member = runCatching {
                                    guild.retrieveMemberById(discordId).await()
                                }.getOrNull()

                                if (member != null) {
                                    guild.addRoleToMember(member, role)
                                        .reason("Premium active")
                                        .await()
                                }
                            } catch (e: Exception) {
                                logger.warn(
                                    "Failed to add premium role to user {} / uuid {} in guild {}",
                                    discordId,
                                    entry.key,
                                    guild.id,
                                    e
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}