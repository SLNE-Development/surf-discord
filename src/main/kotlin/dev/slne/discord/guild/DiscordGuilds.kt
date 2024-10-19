package dev.slne.discord.guild

import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.guild.permission.TicketViewPermission
import dev.slne.discord.guild.role.DiscordRolePermissions
import dev.slne.discord.ticket.TicketType

fun getDiscordGuildByGuildId(guildId: String) =
    DiscordGuilds.entries.firstOrNull { it.discordGuild.guildId == guildId }

enum class DiscordGuilds(val discordGuild: DiscordGuild) {

    CASTCRAFTER(
        DiscordGuild(
            guildId = "133198459531558912",
            whitelistRoleId = "1052361599484170300",
            ticketChannels = mapOf(
                TicketType.WHITELIST to "",
                TicketType.DISCORD_SUPPORT to "",
                TicketType.UNBAN to "",
                TicketType.REPORT to "",
                TicketType.BUGREPORT to "",
                TicketType.EVENT_SUPPORT to "",
                TicketType.SURVIVAL_SUPPORT to ""
            ),
            roles = listOf(
                // Discord Admin
                DiscordRolePermissions(
                    discordRoleIds = listOf("156159112416067584"),
                    ticketViewPermissions = TicketViewPermission.entries,
                    commandPermissions = listOf(
                        CommandPermission.NO_INTEREST,
                        CommandPermission.WHITELIST,
                        CommandPermission.WHITELISTED,
                        CommandPermission.WHITELIST_QUERY,
                        CommandPermission.TICKET_ADD_USER,
                        CommandPermission.TICKET_REMOVE_USER,
                        CommandPermission.TICKET_CLOSE
                    ),
                ),
                // Discord Moderator
                DiscordRolePermissions(
                    discordRoleIds = listOf("156164562499010560"),
                    ticketViewPermissions = TicketViewPermission.entries,
                    commandPermissions = listOf(
                        CommandPermission.NO_INTEREST,
                        CommandPermission.WHITELIST,
                        CommandPermission.WHITELISTED,
                        CommandPermission.WHITELIST_QUERY,
                        CommandPermission.TICKET_ADD_USER,
                        CommandPermission.TICKET_REMOVE_USER,
                        CommandPermission.TICKET_CLOSE
                    ),
                ),
                // Server Admin
                DiscordRolePermissions(
                    discordRoleIds = listOf("949704206888079490"),
                    ticketViewPermissions = TicketViewPermission.entries,
                    commandPermissions = CommandPermission.entries,
                ),
                // Server Developer
                DiscordRolePermissions(
                    discordRoleIds = listOf("1242929429747994664"),
                    ticketViewPermissions = listOf(
                        TicketViewPermission.VIEW_BUGREPORT_TICKETS
                    ),
                    commandPermissions = listOf(
                        CommandPermission.NO_INTEREST,
                        CommandPermission.WHITELIST,
                        CommandPermission.WHITELISTED,
                        CommandPermission.WHITELIST_QUERY,
                        CommandPermission.TICKET_ADD_USER,
                        CommandPermission.TICKET_REMOVE_USER,
                        CommandPermission.TICKET_CLOSE
                    ),
                ),
                // Server Moderator
                DiscordRolePermissions(
                    discordRoleIds = listOf("1242929223593758811"),
                    ticketViewPermissions = listOf(
                        TicketViewPermission.VIEW_WHITELIST_TICKETS,
                        TicketViewPermission.VIEW_SURVIVAL_SUPPORT_TICKETS,
                        TicketViewPermission.VIEW_EVENT_SUPPORT_TICKETS,
                        TicketViewPermission.VIEW_UNBAN_TICKETS,
                        TicketViewPermission.VIEW_REPORT_TICKETS
                    ),
                    commandPermissions = listOf(
                        CommandPermission.NO_INTEREST,
                        CommandPermission.WHITELIST,
                        CommandPermission.WHITELISTED,
                        CommandPermission.WHITELIST_QUERY,
                        CommandPermission.TICKET_ADD_USER,
                        CommandPermission.TICKET_REMOVE_USER,
                        CommandPermission.TICKET_CLOSE
                    ),
                ),
                // Server Supporter
                DiscordRolePermissions(
                    discordRoleIds = listOf("1242929497041277099"),
                    ticketViewPermissions = listOf(
                        TicketViewPermission.VIEW_WHITELIST_TICKETS,
                        TicketViewPermission.VIEW_SURVIVAL_SUPPORT_TICKETS,
                        TicketViewPermission.VIEW_EVENT_SUPPORT_TICKETS,
                        TicketViewPermission.VIEW_UNBAN_TICKETS
                    ),
                    commandPermissions = listOf(
                        CommandPermission.NO_INTEREST,
                        CommandPermission.WHITELIST,
                        CommandPermission.WHITELISTED,
                        CommandPermission.WHITELIST_QUERY,
                        CommandPermission.TICKET_ADD_USER,
                        CommandPermission.TICKET_REMOVE_USER,
                        CommandPermission.TICKET_CLOSE,
                        CommandPermission.HOW_TO_JOIN,
                        CommandPermission.DONT_ASK_TO_ASK,
                        CommandPermission.FAQ
                    ),
                ),
            )
        )
    ),
    SLNE(
        DiscordGuild(
            guildId = "449314616628084758",
            whitelistRoleId = "1052580474712756244",
            ticketChannels = mapOf(
                TicketType.WHITELIST to "1293932444675735602",
                TicketType.DISCORD_SUPPORT to "1293932762029359164",
                TicketType.UNBAN to "1293933419100635248",
                TicketType.REPORT to "1293932647982305342",
                TicketType.BUGREPORT to "1293932673416302664",
                TicketType.EVENT_SUPPORT to "1293932508651716638",
                TicketType.SURVIVAL_SUPPORT to "1293932485000040531"
            ),
            roles = listOf(
                DiscordRolePermissions(
                    discordRoleIds = listOf("449314761386098688"),
                    ticketViewPermissions = TicketViewPermission.entries,
                    commandPermissions = CommandPermission.entries,
                ),
                // Developer
                DiscordRolePermissions(
                    discordRoleIds = listOf("998912043862208532"),
                    commandPermissions = listOf(
                        CommandPermission.DONT_ASK_TO_ASK,
                        CommandPermission.HOW_TO_JOIN
                    )
                )
            )
        )
    );
}
