package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.util.PlayerLookupService
import java.util.*

data class SocialEntry(
    val webUserId: UUID,
    val discordId: Long,
    val minecraftUuid: UUID,
    val blocked: Boolean = false
) {
    private val playerLookupService by lazy { getBean<PlayerLookupService>() }
    suspend fun getMinecraftName() = playerLookupService.getUsername(minecraftUuid)
}
