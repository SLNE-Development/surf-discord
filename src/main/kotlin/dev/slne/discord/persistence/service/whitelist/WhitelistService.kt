package dev.slne.discord.persistence.service.whitelist

import dev.slne.discord.persistence.feign.dto.WhitelistDTO
import net.dv8tion.jda.api.entities.User
import java.util.*

object WhitelistService {

    suspend fun updateWhitelist(whitelist: WhitelistDTO): WhitelistDTO? = null

    suspend fun addWhitelist(whitelist: WhitelistDTO): WhitelistDTO? = null

    suspend fun getWhitelistByDiscordId(discordId: String): WhitelistDTO? = null

    suspend fun checkWhitelists(
        uuid: UUID? = null,
        discordId: String? = null,
        twitchLink: String? = null
    ): List<WhitelistDTO> = emptyList()

    suspend fun isWhitelisted(
        uuid: UUID?,
        discordId: String?,
        twitchLink: String?
    ): Boolean = true

    suspend fun isWhitelisted(user: User): Boolean = true
}
