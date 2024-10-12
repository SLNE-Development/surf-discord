package dev.slne.discord.spring.service.whitelist

import dev.slne.discord.spring.feign.dto.WhitelistDTO
import net.dv8tion.jda.api.entities.User
import java.util.*

object WhitelistService {

    suspend fun updateWhitelist(whitelist: WhitelistDTO): WhitelistDTO? = TODO("Implement")

    suspend fun addWhitelist(whitelist: WhitelistDTO): WhitelistDTO? = TODO("Implement")

    suspend fun getWhitelistByDiscordId(discordId: String): WhitelistDTO? = TODO("Implement")

    suspend fun checkWhitelists(
        uuid: UUID?,
        discordId: String?,
        twitchLink: String?
    ): List<WhitelistDTO> = TODO("Implement")

    suspend fun isWhitelisted(
        uuid: UUID?,
        discordId: String?,
        twitchLink: String?
    ): Boolean = TODO("Implement")

    suspend fun isWhitelisted(user: User): Boolean = TODO("Implement")
}
