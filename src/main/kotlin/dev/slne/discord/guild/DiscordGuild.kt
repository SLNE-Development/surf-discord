package dev.slne.discord.guild

import dev.slne.discord.guild.role.DiscordRolePermissions
import dev.slne.discord.jda
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Role

data class DiscordGuild(
    val guildId: String,
    val whitelistRoleId: String,

    val roles: List<DiscordRolePermissions>,
    val ticketChannels: Map<TicketType, String>
) {
    val whitelistRole: Role?
        get() = jda.getGuildById(guildId)?.getRoleById(whitelistRoleId)
}