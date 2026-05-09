package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.util.AuditableLongIdTable


object SocialConnectionsTable : AuditableLongIdTable("social_connections_new") {
    val minecraftUuid = nativeUuid("minecraft_uuid").uniqueIndex()
    val discordUserId = long("discord_user_id").uniqueIndex().nullable()
    val twitchId = long("twitch_id").uniqueIndex().nullable()
}

object FreebuildWhitelistTable : AuditableLongIdTable("freebuild_whitelists") {
    val socialConnectionId =
        long("social_connection_id").references(SocialConnectionsTable.id).uniqueIndex()
    val blocked = bool("blocked").default(false)
}