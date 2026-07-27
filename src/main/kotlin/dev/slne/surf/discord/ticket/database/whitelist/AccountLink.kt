package dev.slne.surf.discord.ticket.database.whitelist

import java.util.*

/**
 * A completed link between a minecraft and a discord account, established by the website.
 *
 * @param webUserId the website user both accounts belong to
 */
data class AccountLink(
    val webUserId: UUID,
    val discordId: Long,
    val minecraftUuid: UUID
)
