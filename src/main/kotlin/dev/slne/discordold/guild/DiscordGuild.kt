package dev.slne.discordold.guild

import dev.slne.discordold.getBean
import dev.slne.discordold.guild.role.DiscordRolePermissions
import dev.slne.discordold.ticket.TicketType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role

data class DiscordGuild(
    val guildId: String,
    val whitelistRoleId: String,

    val roles: List<DiscordRolePermissions>,
    val ticketChannels: Map<TicketType, String>
) {
    val whitelistRole: Role?
        get() = getBean<JDA>().getGuildById(guildId)?.getRoleById(whitelistRoleId)
}