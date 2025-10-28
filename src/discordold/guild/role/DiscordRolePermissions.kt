package dev.slne.discordold.guild.role

import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.guild.permission.TicketViewPermission
import dev.slne.discordold.ticket.TicketType

data class DiscordRolePermissions(
    val discordRoleIds: List<String>,
    val ticketViewPermissions: List<TicketViewPermission> = emptyList(),
    private val commandPermissions: List<CommandPermission> = emptyList(),
    val defaultRole: Boolean = false
) {

    fun hasCommandPermission(commandPermission: CommandPermission) =
        commandPermission in commandPermissions

    fun hasDiscordPermission(permission: TicketViewPermission) = permission in ticketViewPermissions

    fun canViewTicketType(ticketType: TicketType?) =
        if (ticketType == null) false else ticketType.viewPermission in ticketViewPermissions
}