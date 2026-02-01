package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.column.offsetDateTime
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import java.util.*

object WhitelistTable : LongIdTable("discord_whitelists") {
    val discordUserId = long("discord_user_id").uniqueIndex()
    val minecraftUuid =
        varchar("minecraft_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
            .uniqueIndex()
    val createdAt = offsetDateTime("created_at")
    val updatedAt = offsetDateTime("updated_at")
}