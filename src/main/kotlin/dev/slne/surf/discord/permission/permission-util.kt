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
            DiscordPermission.COMMAND_TICKET_REMOVE
        )
    ),
    // CastCrafter Discord
    133198459531558912 to mapOf(
        // Server Admin
        949704206888079490 to setOf(*DiscordPermission.entries.toTypedArray()),

        // Twitch Mod
        651104534529179660 to setOf(
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_SUPPORT_TWITCH_VIEW
        ),


        // Discord Moderation
        156164562499010560 to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_ADD_SILENT,
            DiscordPermission.COMMAND_TICKET_ADD_ROLE,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.COMMAND_TICKET_BUTTONS,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_CLOSE_BYPASS_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_SUPPORT_DISCORD_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.TICKET_BUG_VIEW,
            DiscordPermission.TICKET_SUPPORT_TWITCH_VIEW,
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.COMMAND_FAQ,
            DiscordPermission.WHITELIST_VIEW,
            DiscordPermission.WHITELIST_BYPASS,
            DiscordPermission.WHITELIST_EDIT,
            DiscordPermission.TICKET_APPLICATION_TWITCH_MODERATOR,
            DiscordPermission.WHITELIST_DELETE,
            DiscordPermission.TICKET_COMPLAINT_VIEW
        ),

        // Management
        1350468732555755570L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_ADD_SILENT,
            DiscordPermission.COMMAND_TICKET_ADD_ROLE,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLOSE_BYPASS_CLAIM,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.TICKET_APPLICATION_SUPPORTER,
            DiscordPermission.TICKET_APPLICATION_BUILDER,
            DiscordPermission.TICKET_APPLICATION_DESIGNER,
            DiscordPermission.TICKET_APPLICATION_DEVELOPER,
            DiscordPermission.COMMAND_FAQ,
            DiscordPermission.WHITELIST_VIEW,
            DiscordPermission.WHITELIST_BYPASS,
            DiscordPermission.WHITELIST_EDIT,
            DiscordPermission.WHITELIST_DELETE,
            DiscordPermission.TICKET_COMPLAINT_VIEW
        ),

        // Developer
        1242929429747994664L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_ADD_SILENT,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.COMMAND_TICKET_BUTTONS,
            DiscordPermission.TICKET_TYPE_BYPASS,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLOSE_BYPASS_CLAIM,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_BUG_VIEW,
            DiscordPermission.TICKET_APPLICATION_BUILDER,
            DiscordPermission.TICKET_APPLICATION_DESIGNER,
            DiscordPermission.TICKET_APPLICATION_DEVELOPER,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.COMMAND_FAQ,
            DiscordPermission.WHITELIST_VIEW,
            DiscordPermission.WHITELIST_BYPASS,
            DiscordPermission.WHITELIST_EDIT,
            DiscordPermission.WHITELIST_DELETE
        ),

        // Moderator
        1242929223593758811L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.TICKET_UNBAN_VIEW,
            DiscordPermission.COMMAND_FAQ,
            DiscordPermission.WHITELIST_VIEW,
            DiscordPermission.WHITELIST_EDIT
        ),

        // Supporter
        1242929497041277099L to setOf(
            DiscordPermission.COMMAND_TICKET_ADD,
            DiscordPermission.COMMAND_TICKET_REMOVE,
            DiscordPermission.TICKET_CLOSE,
            DiscordPermission.TICKET_CLAIM,
            DiscordPermission.TICKET_WHITELIST_VIEW,
            DiscordPermission.TICKET_REPLY_DEADLINE,
            DiscordPermission.TICKET_WHITELIST_CONFIRM,
            DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
            DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
            DiscordPermission.TICKET_REPORT_VIEW,
            DiscordPermission.COMMAND_FAQ,
            DiscordPermission.WHITELIST_VIEW
        ),

        // Community Management
        1403107386415386736L to setOf(
            DiscordPermission.COMMAND_FAQ
        ),

        // Builder
        1242929846481453107L to setOf(
            DiscordPermission.COMMAND_FAQ
        ),

        // Server Team
        1001166778287792158L to setOf(
            DiscordPermission.COMMAND_FAQ
        )
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

