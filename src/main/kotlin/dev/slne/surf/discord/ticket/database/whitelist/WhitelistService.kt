package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.util.PlayerLookupService
import org.springframework.stereotype.Service

@Service
class WhitelistService(
    private val whitelistRepository: WhitelistRepository,
    private val playerLookupService: PlayerLookupService
) {
    suspend fun isWhitelisted(discordId: Long) = whitelistRepository.isWhitelisted(discordId)
    suspend fun isWhitelisted(minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            whitelistRepository.isWhitelisted(it)
        } ?: false

    suspend fun whitelist(discordId: Long, minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            whitelistRepository.whitelist(discordId, it)
        }

    suspend fun blockWhitelist(discordId: Long) = whitelistRepository.blockWhitelist(discordId)
    suspend fun unblockWhitelist(discordId: Long) = whitelistRepository.unblockWhitelist(discordId)

    suspend fun getWhitelist(discordId: Long) = whitelistRepository.getWhitelist(discordId)
}