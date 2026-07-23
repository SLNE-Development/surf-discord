package dev.slne.surf.discord.ticket.database.whitelist

import dev.slne.surf.discord.ticket.database.column.nativeUuid
import dev.slne.surf.discord.ticket.database.util.AuditableLongIdTable

object SocialConnectionsTable : AuditableLongIdTable("surf-social.social_connections") {
    val minecraftUuid = nativeUuid("minecraft_uuid").uniqueIndex()
    val discordUserId = long("discord_user_id").uniqueIndex().nullable()
    val twitchId = long("twitch_id").uniqueIndex().nullable()
}

object FreebuildWhitelistTable :
    AuditableLongIdTable("surf-whitelist.freebuild_whitelists") {
    val socialConnectionId = reference("social_connection_id", SocialConnectionsTable).uniqueIndex()
    val blocked = bool("blocked").default(false)
}
