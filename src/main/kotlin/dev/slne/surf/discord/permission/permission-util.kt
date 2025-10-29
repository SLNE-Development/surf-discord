package dev.slne.surf.discord.permission

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

private val guildPermissionConfig: Map<Long, Map<Long, Set<DiscordPermission>>> = mapOf(
    1410944184231137332 to mapOf(// Guild ID
        1432660473916035133 to setOf(
            DiscordPermission.COMMAND_TICKET_BUTTONS
        ),// Admin
        1432660626643222528 to setOf(DiscordPermission.TICKET_WHITELIST_VIEW) // Supporter
    )
)

fun User.hasPermission(guildId: Long, permission: DiscordPermission): Boolean {
    val guild = jda.getGuildById(guildId) ?: return false
    val member = guild.getMember(this) ?: return false

    return member.hasPermission(permission)
}

fun Member?.hasPermission(permission: DiscordPermission): Boolean {
    if (this == null) {
        return false
    }

    val guildPerms = guildPermissionConfig[guild.idLong] ?: return false
    val memberRoleIds = roles.map { it.idLong }

    return memberRoleIds.any { roleId ->
        guildPerms[roleId]?.contains(permission) == true
    }
}

fun DiscordPermission.getRolesWithPermission(guildId: Long) =
    guildPermissionConfig[guildId]?.filterValues { this in it }?.keys ?: emptySet()

