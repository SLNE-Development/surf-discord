package dev.slne.discord

import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.select.DiscordSelectMenuManager
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Component

@Component
class DiscordBot {

    private val logger = ComponentLogger.logger()

    @PostConstruct
    suspend fun onLoad() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logger.error("Uncaught exception in thread ${thread.name}", throwable)
        }

        initObjects()

        logger.info("Discord Bot is ready")
    }

    private fun initObjects() {
        DiscordModalManager
        DiscordSelectMenuManager
    }

    @PreDestroy
    fun onDisable() {
        // Currently empty
    }
}
