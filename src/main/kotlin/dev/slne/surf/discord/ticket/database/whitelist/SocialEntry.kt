package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.util.PlayerLookupService
import java.util.*

data class SocialEntry(
    val webUserId: UUID,
    val discordId: Long,
    val minecraftUuid: UUID,
    val blocked: Boolean = false
) {
    suspend fun getMinecraftName() = PlayerLookupService.getUsername(minecraftUuid)
}
