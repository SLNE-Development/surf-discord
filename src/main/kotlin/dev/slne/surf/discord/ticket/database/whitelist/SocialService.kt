package dev.slne.surf.discord.ticket.database.whitelist

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

    suspend fun updateWhitelist(
        discordId: Long,
        minecraftName: String,
        blocked: Boolean
    ) {
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.editWhitelist(discordId, it, blocked)
        }
    }

    suspend fun deleteWhitelist(discordId: Long) = socialRepository.deleteWhitelist(discordId)
}