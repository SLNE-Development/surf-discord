package dev.slne.discord

import dev.minn.jda.ktx.jdabuilder.cache
import dev.minn.jda.ktx.jdabuilder.default
import dev.slne.discord.config.botConfig
import dev.slne.discord.discord.interaction.command.DiscordCommandManager
import dev.slne.discord.discord.interaction.modal.ChannelCreationModalManager
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager
import dev.slne.discord.listener.DiscordListenerManager
import dev.slne.discord.settings.GatewayIntents
import dev.slne.discord.spring.processor.DiscordButtonManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object DiscordBot {

    private val logger = ComponentLogger.logger()

    lateinit var jda: JDA

    fun onLoad() {
        val botToken = botConfig.botToken

        if (botToken == null) {
            logger.error("Bot token is null. Please check your bot-connection.json file.")
            ExitCodes.BOT_TOKEN_NOT_SET.exit()
        }

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logger.error("Uncaught exception in thread ${thread.name}", throwable)
        }

        this.jda = default(
            token = botToken,
            intents = GatewayIntents.gatewayIntents
        ) {
            setAutoReconnect(true)

            cache += listOf(
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOJI,
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.ONLINE_STATUS,
                CacheFlag.ROLE_TAGS,
                CacheFlag.STICKER,
                CacheFlag.SCHEDULED_EVENTS
            )
            cache -= CacheFlag.VOICE_STATE

            setStatus(OnlineStatus.DO_NOT_DISTURB)
        }

        try {
            jda.awaitReady()
        } catch (exception: InterruptedException) {
            logger.error("Failed to await ready.", exception)
            ExitCodes.FAILED_AWAIT_READY_JDA.exit()
        }

        initObjects()
        logger.info("Discord Bot is ready")
    }

    private fun initObjects() {
        DiscordListenerManager
        ChannelCreationModalManager
        DiscordButtonManager
        DiscordCommandManager
        DiscordSelectMenuManager
    }

    fun onEnable() {

    }

    fun onDisable() {
        // Currently empty
    }
}
