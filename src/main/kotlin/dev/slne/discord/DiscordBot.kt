package dev.slne.discord

import dev.minn.jda.ktx.events.CoroutineEventManager
import dev.minn.jda.ktx.events.getDefaultScope
import dev.minn.jda.ktx.jdabuilder.cache
import dev.minn.jda.ktx.jdabuilder.default
import dev.slne.discord.config.botConfig
import dev.slne.discord.settings.GatewayIntents
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class DiscordBot {

    private val logger = ComponentLogger.logger()

    @Bean(destroyMethod = "shutdown")
    fun jda(): JDA {
        val botToken = botConfig.botToken

        val jda = default(
            token = botToken,
            intents = GatewayIntents.gatewayIntents,
            enableCoroutines = false
        ) {
            setEventManager(CoroutineEventManager(scope = getDefaultScope(context = Dispatchers.IO)))
            setAutoReconnect(true)
            setBulkDeleteSplittingEnabled(true)

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

        return jda
    }
}