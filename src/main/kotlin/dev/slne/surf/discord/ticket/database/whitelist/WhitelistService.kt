package dev.slne.surf.discord.ticket.database.whitelist

import org.springframework.stereotype.Service

@Service
class WhitelistService {
    suspend fun isWhitelisted(discordId: Long) = false
    suspend fun isWhitelisted(minecraftName: String) = false

    suspend fun whitelist(discordId: Long, minecraftName: String) = WhitelistEntry.empty()
    suspend fun blockWhitelist(discordId: Long) = Unit
    suspend fun unblockWhitelist(discordId: Long) = Unit

    suspend fun getWhitelist(discordId: Long) = WhitelistEntry.empty()
}