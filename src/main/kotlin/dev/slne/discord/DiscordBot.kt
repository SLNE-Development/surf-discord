package dev.slne.discord

import dev.slne.data.api.DataApi
import dev.slne.discord.config.BotConfig
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager
import dev.slne.discord.discord.settings.GatewayIntents
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag

/**
 * The type Discord bot.
 */
object DiscordBot {

    lateinit var jda: JDA
    private lateinit var selectMenuManager: DiscordSelectMenuManager

    /**
     * Called when the bot is loaded.
     */
    fun onLoad() {
        val botConfig = BotConfig()
        val botToken = botConfig.discordBotConfig.botToken

        if (botToken == null) {
            DataApi.getDataInstance()
                .logError(
                    javaClass,
                    "Bot token is null. Please check your bot-connection.json file."
                )
            System.exit(1000)
            return
        }

        Thread.setDefaultUncaughtExceptionHandler { thread: Thread, throwable: Throwable? ->
            DataApi.getDataInstance()
                .logError(javaClass, "Uncaught exception in thread " + thread.name, throwable)
        }

        val builder: JDABuilder = JDABuilder.createDefault(botToken)

        builder.setAutoReconnect(true)
        builder.enableCache(
            CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS, CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS
        )
        builder.disableCache(CacheFlag.VOICE_STATE)
        builder.setEnabledIntents(GatewayIntents.getGatewayIntents())
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB)

        this.jda = builder.build()
        try {
            jda.awaitReady()
        } catch (exception: InterruptedException) {
            DataApi.getDataInstance().logError(javaClass, "Failed to await ready.", exception)
        }

        selectMenuManager = DiscordSelectMenuManager()

        DataApi.getDataInstance().logInfo(javaClass, "Discord Bot is ready")
    }

    /**
     * Called when the bot is enabled.
     */
    fun onEnable() {

    }

    /**
     * Called when the bot is disabled.
     */
    fun onDisable() {
        // Currently empty
    }
}
