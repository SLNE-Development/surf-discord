package dev.slne.surf.discord.ticket.database.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.util.PlayerLookupService
import org.springframework.stereotype.Service

@Service
class SocialService(
    private val socialRepository: SocialRepository,
    private val playerLookupService: PlayerLookupService
) {
    suspend fun isWhitelisted(discordId: Long) = socialRepository.isWhitelisted(discordId)
    suspend fun isWhitelisted(minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.isWhitelisted(it)
        } ?: false

    suspend fun whitelist(discordId: Long, minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.whitelist(discordId, it)
        }

    suspend fun blockWhitelist(discordId: Long) = socialRepository.blockWhitelist(discordId)
    suspend fun unblockWhitelist(discordId: Long) = socialRepository.unblockWhitelist(discordId)

    suspend fun getWhitelist(discordId: Long) = socialRepository.getWhitelist(discordId)
    suspend fun getWhitelist(minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.getWhitelist(it)
        }

    suspend fun updateBlocked(discordId: Long, blocked: Boolean): Boolean =
        socialRepository.editBlocked(discordId, blocked)

    suspend fun updateMinecraftName(discordId: Long, minecraftName: String): Boolean {
        return playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.editMinecraftName(discordId, it)
        } != null
    }

    suspend fun updateDiscordId(oldDiscordId: Long, discordId: Long): Boolean {
        return runCatching {
            val oldDiscordUser = jda.retrieveUserById(oldDiscordId).await()
            val newDiscordUser = jda.retrieveUserById(discordId).await()

            jda.guilds.forEach { guild ->
                val role = guild.getRoleById(botConfig.whitelistedRoleId)
                    ?: return@runCatching false

                guild.removeRoleFromMember(oldDiscordUser, role).queue()
                guild.addRoleToMember(newDiscordUser, role).queue()
            }

            socialRepository.editDiscordId(oldDiscordId, discordId)
            true
        }.getOrElse {
            false
        }
    }

    suspend fun deleteWhitelist(discordId: Long) = socialRepository.deleteWhitelist(discordId)
}