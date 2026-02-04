package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object WhitelistTable : LongIdTable("discord_whitelists") {
    val discordUserId = long("discord_user_id").uniqueIndex()
    val minecraftUuid = nativeUuid("minecraft_uuid").uniqueIndex()
    val blocked = bool("blocked").default(false)
    val createdAt = offsetDateTime("created_at")
    val updatedAt = offsetDateTime("updated_at")
}