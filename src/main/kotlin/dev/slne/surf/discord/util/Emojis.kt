package dev.slne.surf.discord.util

import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.emoji.Emoji

class Emojis(
    private val jda: JDA
) {
    lateinit var checkMark: Emoji
    lateinit var crossMark: Emoji
    lateinit var information: Emoji

    @PostConstruct
    fun updateEmojis() {
        val check = jda.getEmojisByName("checkmark", true).firstOrNull()
            ?: error("Checkmark emoji not found")
        val cross = jda.getEmojisByName("crossmark", true).firstOrNull()
            ?: error("Crossmark emoji not found")
        val info = jda.getEmojisByName("information", true).firstOrNull()
            ?: error("Information emoji not found")

        checkMark = Emoji.fromCustom(check.name, check.idLong, check.isAnimated)
        crossMark = Emoji.fromCustom(cross.name, cross.idLong, cross.isAnimated)
        information = Emoji.fromCustom(info.name, info.idLong, info.isAnimated)
    }
}