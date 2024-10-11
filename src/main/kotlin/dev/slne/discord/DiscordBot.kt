package dev.slne.discord

import dev.slne.discord.config.botConfig
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager
import dev.slne.discord.settings.GatewayIntents
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object DiscordBot {

    private var logger = ComponentLogger.logger(DiscordBot::class.java)

    lateinit var jda: JDA
    lateinit var selectMenuManager: DiscordSelectMenuManager

    fun onLoad() {
        val botToken = botConfig.botToken

        if (botToken == null) {
            logger.error("Bot token is null. Please check your bot-connection.json file.")
            System.exit(1000)
            return
        }

        Thread.setDefaultUncaughtExceptionHandler { thread: Thread, throwable: Throwable? ->
            logger.error("Uncaught exception in thread ${thread.name}", throwable)
        }

        val builder = JDABuilder.createDefault(botToken).apply {
            setAutoReconnect(true)
            enableCache(
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOJI,
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ONLINE_STATUS,
                CacheFlag.ROLE_TAGS,
                CacheFlag.STICKER,
                CacheFlag.SCHEDULED_EVENTS
            )
            disableCache(CacheFlag.VOICE_STATE)
            setEnabledIntents(GatewayIntents.gatewayIntents)
            setStatus(OnlineStatus.DO_NOT_DISTURB)
        }

        this.jda = builder.build()
        try {
            jda.awaitReady()
        } catch (exception: InterruptedException) {
            logger.error("Failed to await ready.", exception)
        }

        selectMenuManager =
            DiscordSelectMenuManager()
        logger.info("Discord Bot is ready")
    }

    fun onEnable() {

    }

    fun onDisable() {
        // Currently empty
    }
}
