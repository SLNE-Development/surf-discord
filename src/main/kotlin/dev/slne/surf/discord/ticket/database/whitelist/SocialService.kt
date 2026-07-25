package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.util.PlayerLookupService
import org.springframework.stereotype.Service

@Service
class SocialService(
    private val socialRepository: SocialRepository,
    private val playerLookupService: PlayerLookupService
) {
    /**
     * The website established link for [discordId], or `null` if the user has not connected their
     * minecraft account yet.
     */
    suspend fun findLink(discordId: Long) = socialRepository.findLinkByDiscordId(discordId)

    suspend fun isWhitelisted(discordId: Long) = socialRepository.isWhitelisted(discordId)
    suspend fun isWhitelisted(minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.isWhitelisted(it)
        } ?: false

    suspend fun whitelist(link: AccountLink) = socialRepository.whitelist(link)

    suspend fun blockWhitelist(discordId: Long) = socialRepository.blockWhitelist(discordId)
    suspend fun unblockWhitelist(discordId: Long) = socialRepository.unblockWhitelist(discordId)

    suspend fun getWhitelist(discordId: Long) = socialRepository.getWhitelist(discordId)
    suspend fun getWhitelist(minecraftName: String) =
        playerLookupService.getUuid(minecraftName)?.let {
            socialRepository.getWhitelist(it)
        }
}
