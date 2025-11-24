package dev.slne.surf.discord.permission

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

private val guildPermissionConfig: Map<Long, Map<Long, Set<DiscordPermission>>> = mapOf(
    // Arty Development Discord (Red)
    1410944184231137332 to mapOf(
        // Admin
        1432660473916035133 to setOf(*DiscordPermission.entries.toTypedArray()),

        // Supporter
        1432660626643222528 to setOf(
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT,
            DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE
        )
    ),
    // CastCrafter Discord
    133198459531558912 to mapOf(
        // Server Admin
        949704206888079490 to setOf(*DiscordPermission.entries.toTypedArray()),

        // Discord Moderation
        156164562499010560 to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.COMMAND_TICKET_BUTTONS,
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
            DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT,
            DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE
        ),

        // Management
        1350468732555755570L to setOf(
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
            DiscordPermission.TICKET_APPLICATION_SUPPORTER,
            DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT,
            DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE
        ),

        // Developer
        1242929429747994664L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.COMMAND_TICKET_BUTTONS,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_BUG_VIEW,
            DiscordPermission.TICKET_APPLICATION_BUILDER,
            DiscordPermission.TICKET_APPLICATION_DEVELOPER,
            DiscordPermission.TICKET_APPLICATION_DESIGNER,
            DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT,
            DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE
        ),

        // Moderator
        1242929223593758811L to setOf(
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

        // Supporter
        1242929497041277099L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
        ),

        // Community Management
        1403107386415386736L to setOf(
            DiscordPermission.COMMAND_ANNOUNCEMENT_CREATE,
            DiscordPermission.COMMAND_ANNOUNCEMENT_EDIT,
            DiscordPermission.COMMAND_ANNOUNCEMENT_DELETE
        ),

        // Builder
        1242929846481453107L to setOf(),

        // Server Team
        1001166778287792158L to setOf()
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

