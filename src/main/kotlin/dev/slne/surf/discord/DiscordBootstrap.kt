package dev.slne.surf.discord

import dev.slne.surf.discord.util.Emojis
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component

@Component
class DiscordBootstrap {
    @PostConstruct
    fun onLoad() {
        logger.info("Loading Discord Bot...")

        updateEmojis()
    }

    @PreDestroy
    fun onDisable() {
        logger.info("Stopping Discord Bot...")
    }

    fun updateEmojis() {
        Emojis.checkMark = jda.getEmojisByName("checkmark", true).firstOrNull()
            ?: error("Checkmark emoji not found")
        Emojis.crossMark = jda.getEmojisByName("crossmark", true).firstOrNull()
            ?: error("Crossmark emoji not found")
        Emojis.information = jda.getEmojisByName("information", true).firstOrNull()
            ?: error("Information emoji not found")
    }
}