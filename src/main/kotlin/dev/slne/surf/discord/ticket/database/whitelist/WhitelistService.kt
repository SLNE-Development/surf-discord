package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
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
        }.also {
            jda.guilds.forEach { guild ->
                guild.getMemberById(discordId)?.let { member ->
                    guild.addRoleToMember(member,
                        guild.getRoleById(botConfig.whitelistedRoleId)
                            ?: error("Whitelisted role not found")
                    ).queue()
                }
            }
        }

    suspend fun blockWhitelist(discordId: Long) = whitelistRepository.blockWhitelist(discordId)
    suspend fun unblockWhitelist(discordId: Long) = whitelistRepository.unblockWhitelist(discordId)

    suspend fun getWhitelist(discordId: Long) = whitelistRepository.getWhitelist(discordId)

    suspend fun updateWhitelist(
        discordId: Long,
        minecraftName: String,
        blocked: Boolean
    ) {
        playerLookupService.getUuid(minecraftName)?.let {
            whitelistRepository.editWhitelist(discordId, it, blocked)
        }
    }
}