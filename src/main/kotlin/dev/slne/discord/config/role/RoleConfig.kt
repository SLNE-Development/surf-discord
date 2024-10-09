package dev.slne.discord.config.role

import dev.slne.discord.config.discord.getGuildConfigByGuildId
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.guild.permission.DiscordPermission
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.Permission
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class RoleConfig {

    lateinit var discordRoleIds: List<String>

    lateinit var discordAllowedPermissions: List<DiscordPermission>
    private lateinit var discordDeniedPermissions: List<DiscordPermission>
    private lateinit var commandAllowedPermissions: List<CommandPermission>
    private lateinit var commandDeniedPermissions: List<CommandPermission>

    var defaultRole: Boolean = false
        private set

    fun hasCommandPermission(commandPermission: CommandPermission) =
        commandPermission in commandAllowedPermissions && commandPermission !in commandDeniedPermissions

    fun hasDiscordPermission(permission: DiscordPermission) =
        permission in discordAllowedPermissions && permission !in discordDeniedPermissions

    fun canViewTicketType(ticketType: TicketType) =
        ticketType.getViewPermissions() in discordAllowedPermissions

    val discordAllowedPermissionsAsJda: List<Permission>
        get() = discordAllowedPermissions.mapNotNull { it.discordPermission }

    val discordDeniedPermissionsAsJDA: List<Permission>
        get() = discordDeniedPermissions.mapNotNull { it.discordPermission }
}

fun getRoleConfig(guildId: String?, roleName: String?) =
    getGuildConfigByGuildId(guildId)?.roleConfig?.get(roleName)

fun getDiscordRoleConfigsByGuildId(guildId: String?, roleId: String?) =
    getGuildConfigByGuildId(guildId)?.roleConfig?.values?.filter { roleId in it.discordRoleIds }
        ?: emptyList()

fun getDefaultRole(guildId: String?) =
    getGuildConfigByGuildId(guildId)?.roleConfig?.values?.find { it.defaultRole }

