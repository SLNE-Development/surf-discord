package dev.slne.discord.guild

import dev.slne.discord.DiscordBot
import dev.slne.discord.guild.role.DiscordRolePermissions
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Role

class DiscordGuild(
    val guildId: String,
    val whitelistRoleId: String,

    val roles: List<DiscordRolePermissions>,
    val ticketChannels: Map<TicketType, String>
) {
    val whitelistRole: Role?
        get() = DiscordBot.jda.getGuildById(guildId)?.getRoleById(whitelistRoleId)
}