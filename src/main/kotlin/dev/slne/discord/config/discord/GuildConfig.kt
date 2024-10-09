package dev.slne.discord.config.discord

import dev.slne.discord.DiscordBot
import dev.slne.discord.config.botConfig
import dev.slne.discord.config.role.RoleConfig
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class GuildConfig {
    lateinit var guildId: String
    lateinit var categoryId: String
    lateinit var whitelistRoleId: String

    val roleConfig: Map<String, RoleConfig>? = null
        get() = Object2ObjectMaps.unmodifiable(Object2ObjectOpenHashMap(field))

    val whitelistedRole: Role?
        get() = DiscordBot.jda.getRoleById(whitelistRoleId)
}


fun getGuildConfigByGuildId(guildId: String?) = botConfig.guildConfig.values
    .firstOrNull { guildConfig -> guildConfig.guildId == guildId }

fun Guild.getGuildConfig() = getGuildConfigByGuildId(id)

