package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.util.PlayerLookupService
import java.time.OffsetDateTime
import java.util.*

data class WhitelistEntry(
    val discordId: Long,
    val minecraftUuid: UUID,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val blocked: Boolean = false
) {
    companion object {
        fun empty(): WhitelistEntry =
            WhitelistEntry(
                discordId = 0L,
                minecraftUuid = UUID(0L, 0L),
                createdAt = OffsetDateTime.MIN,
                updatedAt = OffsetDateTime.MIN
            )
    }

    private val playerLookupService by lazy { getBean<PlayerLookupService>() }
    suspend fun getMinecraftName() = playerLookupService.getUsername(minecraftUuid)
}
