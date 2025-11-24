package dev.slne.surf.discord

import dev.slne.surf.discord.config.botConfig
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import kotlin.system.exitProcess

@Service
class DiscordBot {

    @Bean
    fun jda(): JDA {
        val botToken = botConfig.botToken

        val builder = JDABuilder.createDefault(botToken)

        builder.enableIntents(gatewayIntents)
        builder.setStatus(OnlineStatus.ONLINE)
        builder.setActivity(Activity.playing("castcrafter.de"))

        val jda = builder.build()

        try {
            jda.awaitReady()
            logger.info("Discord Bot is ready.")
        } catch (exception: InterruptedException) {
            logger.error("Failed to await ready.", exception)
            exitProcess(1)
        }

        return jda
    }

    private val gatewayIntents = listOf(
        // Guild
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.SCHEDULED_EVENTS,

        // Guild Messages
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGE_TYPING,

        // Direct Messages
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING
    )

    companion object {
        val SURVIVAL_ENABLED = false
        val EVENT_ENABLED = true
    }
}