package dev.slne.discord.guild

import dev.slne.discord.guild.role.DiscordRolePermissions
import dev.slne.discord.ticket.TicketType

class DiscordGuild(
    val guildId: String,
    val whitelistRoleId: String,

    val roles: List<DiscordRolePermissions>,
    val ticketChannels: Map<TicketType, String>
) {
}