package dev.slne.surf.discord

import dev.slne.surf.discord.util.Emojis
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import net.dv8tion.jda.api.entities.emoji.Emoji
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
        val check = jda.getEmojisByName("checkmark", true).firstOrNull()
            ?: error("Checkmark emoji not found")
        val cross = jda.getEmojisByName("crossmark", true).firstOrNull()
            ?: error("Crossmark emoji not found")
        val info = jda.getEmojisByName("information", true).firstOrNull()
            ?: error("Information emoji not found")

        Emojis.checkMark = Emoji.fromCustom(check.name, check.idLong, check.isAnimated)
        Emojis.crossMark = Emoji.fromCustom(cross.name, cross.idLong, cross.isAnimated)
        Emojis.information = Emoji.fromCustom(info.name, info.idLong, info.isAnimated)
    }

}