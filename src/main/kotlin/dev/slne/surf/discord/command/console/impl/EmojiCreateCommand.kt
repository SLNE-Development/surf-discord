package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommand
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommandContext
import net.dv8tion.jda.api.entities.Icon
import java.io.File

object EmojiCreateCommand : MicroserviceCommand("createartyemojis") {
    override suspend fun MicroserviceCommandContext.execute(
        args: List<String>
    ) {
        val checkmarkFile = File("emojis/checkmark.png")
        val informationFile = File("emojis/information.png")
        val crossMarkFile = File("emojis/crossmark.png")

        if (!checkmarkFile.exists() || !informationFile.exists() || !crossMarkFile.exists()) {
            logger.error("Some Emoji files not found in the 'emojis' directory. (checkmark.png, information.png, crossmark.png)")
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

        logger.info("Created Emojis in all guilds.")
    }
}