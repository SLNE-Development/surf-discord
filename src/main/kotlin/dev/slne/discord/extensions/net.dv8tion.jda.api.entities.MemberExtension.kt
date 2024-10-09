package dev.slne.discord.extensions

import dev.slne.discord.config.discord.GuildConfig
import dev.slne.discord.config.role.RoleConfig
import net.dv8tion.jda.api.entities.Member

fun Member.getRoleIds() = roles.map { it.id }

fun Member.isTeamMember(guildConfig: GuildConfig) =
    guildConfig.roleConfig?.values?.map(RoleConfig::discordRoleIds)?.flatten()
        ?.let { roleIds -> getRoleIds().any { roleIds.contains(it) } } ?: false