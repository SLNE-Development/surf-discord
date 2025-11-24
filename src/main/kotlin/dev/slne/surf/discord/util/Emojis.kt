package dev.slne.surf.discord.util

import dev.slne.surf.discord.logger
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.emoji.Emoji
import org.springframework.stereotype.Component
import java.io.File

@Component
class Emojis(
    private val jda: JDA
) {
    lateinit var checkMark: Emoji
    lateinit var crossMark: Emoji
    lateinit var information: Emoji

    @PostConstruct
    fun updateEmojis() {
        var check = jda.getEmojisByName("checkmark", true).firstOrNull()
        var cross = jda.getEmojisByName("crossmark", true).firstOrNull()
        var info = jda.getEmojisByName("information", true).firstOrNull()

        if (check == null || cross == null || info == null) {
            failure()

            logger.warn("Some required emojis were missing and have been created. Please wait a moment for Discord to process them.")
            Thread.sleep(1500)
            logger.warn("Retrying emoji initialization...")

            check = jda.getEmojisByName("checkmark", true).firstOrNull()
            cross = jda.getEmojisByName("crossmark", true).firstOrNull()
            info = jda.getEmojisByName("information", true).firstOrNull()
        }

        checkMark = Emoji.fromCustom(check!!.name, check.idLong, check.isAnimated)
        crossMark = Emoji.fromCustom(cross!!.name, cross.idLong, cross.isAnimated)
        information = Emoji.fromCustom(info!!.name, info.idLong, info.isAnimated)

        logger.info("Emoji initialization complete.")
    }


    fun failure() {
        logger.warn("Required emojis not found. Creating them...")

        val checkmarkFile = File("emojis/checkmark.png")
        val informationFile = File("emojis/information.png")
        val crossMarkFile = File("emojis/crossmark.png")

        if (!checkmarkFile.exists() || !informationFile.exists() || !crossMarkFile.exists()) {
            logger.error("Some emoji files not found in the 'emojis' directory. (checkmark.png, information.png, crossmark.png)")
            return
        }

        val checkmarkIcon = Icon.from(checkmarkFile)
        val crossMarkIcon = Icon.from(crossMarkFile)
        val informationIcon = Icon.from(informationFile)

        jda.guilds.forEach {
            it.createEmoji("checkmark", checkmarkIcon).queue()
            it.createEmoji("crossmark", crossMarkIcon).queue()
            it.createEmoji("information", informationIcon).queue()
        }

        logger.info("Created emojis in all guilds.")
    }

}