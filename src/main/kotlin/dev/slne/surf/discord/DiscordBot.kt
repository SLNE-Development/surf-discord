package dev.slne.surf.discord

import dev.slne.surf.discord.config.botConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.internal.utils.JDALogger
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.io.path.Path
import kotlin.system.exitProcess

object DiscordBot {
    val dataPath = Path("config")

    lateinit var jda: JDA

    const val SURVIVAL_ENABLED = true
    const val EVENT_ENABLED = true
    const val SUPPORT_APPLICATION_ENABLED = false
    const val TWITCH_APPLICATION_ENABLED = false

    fun createJda(): JDA {
        val botToken = botConfig.botToken

        JDALogger.setFallbackLoggerEnabled(false)
        val builder = JDABuilder.createDefault(botToken)

        builder.enableIntents(gatewayIntents)
        builder.setStatus(OnlineStatus.ONLINE)
        builder.setActivity(Activity.playing("castcrafter.de"))

        val jda = builder.build()

        try {
            jda.awaitReady()
        } catch (exception: InterruptedException) {
            logger.error("Failed to await ready.", exception)
            exitProcess(1)
        }

        DiscordBot.jda = jda
        return jda
    }

    private val gatewayIntents = listOf(
        // Guild
        GatewayIntent.GUILD_MEMBERS,
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
}

val jda get() = DiscordBot.jda
val logger = ComponentLogger.logger("surf-discord")
val discordScope by lazy {
    CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { context, throwable ->
        logger.error(
            "Uncaught exception in coroutine. Context: $context",
            throwable
        )
    })
}

val ticketChannel by lazy {
    jda.getTextChannelById(botConfig.channels.ticketChannel) ?: run {
        logger.error("Ticket channel with ID ${botConfig.channels.ticketChannel} not found!")
        null
    }
}