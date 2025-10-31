package dev.slne.surf.discord.permission

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

private val guildPermissionConfig: Map<Long, Map<Long, Set<DiscordPermission>>> = mapOf(
    1410944184231137332 to mapOf(// Arty Development Discord (Red)
        1432660473916035133 to setOf(*DiscordPermission.entries.toTypedArray()), // Admin
        1432660626643222528 to setOf(
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
        ) // Supporter
    ),
    133198459531558912 to mapOf(// CastCrafter Discord
        949704206888079490 to setOf(*DiscordPermission.entries.toTypedArray()), // Server Admin
        156164562499010560 to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_SUPPORT_DISCORD_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.TICKET_BUG_VIEW,
            DiscordPermission.TICKET_APPLICATION_VIEW
        ), // Discord Moderation
        1350468732555755570L to setOf( // Management
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.TICKET_APPLICATION_VIEW
        ),

        1242929429747994664L to setOf( // Developer
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.TICKET_BUG_VIEW
        ),

        1242929223593758811L to setOf( // Moderator
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW
        ),

        1242929497041277099L to setOf( // Supporter
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW
        ),

        1403107386415386736L to setOf(), // Community Management
        1242929846481453107L to setOf(), // Builder
        1001166778287792158L to setOf() // Server Team
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

