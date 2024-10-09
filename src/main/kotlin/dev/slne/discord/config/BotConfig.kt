package dev.slne.discord.config

import dev.slne.discord.ExitCodes
import dev.slne.discord.config.discord.DiscordBotConfig
import dev.slne.discord.config.discord.GuildConfig
import dev.slne.discord.config.ticket.TicketConfig
import org.jetbrains.annotations.ApiStatus
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

@ApiStatus.Internal
@ConfigSerializable
class BotConfig {
    lateinit var guildConfig: Map<String, GuildConfig>
    lateinit var discordBotConfig: DiscordBotConfig
    lateinit var ticketConfig: TicketConfig
}

val botConfig: BotConfig by lazy { loadConfig() }

/**
 * Load config.
 */
private fun loadConfig(): BotConfig {
    val loader = YamlConfigurationLoader.builder().path(Path.of("data/config.yml")).build()

    try {
        return loader.load()?.get(BotConfig::class.java)
            ?: ExitCodes.CONFIG_FAILED_TO_LOAD.exit()
    } catch (exception: Exception) {
        System.err.println("An error occurred while loading this configuration: " + exception.message)
        exception.cause?.printStackTrace()

        ExitCodes.CONFIG_FAILED_TO_LOAD.exit()
    }
}
