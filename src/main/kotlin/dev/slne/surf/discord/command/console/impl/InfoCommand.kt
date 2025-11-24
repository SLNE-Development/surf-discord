package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import org.springframework.stereotype.Component

@Component
class InfoCommand : ConsoleCommand {
    override val name = "info"

    override fun execute(args: List<String>) {
        logger.info("---- Guild Information ----")
        jda.guilds.forEach {
            logger.info("Connected to guild: ${it.name} (ID: ${it.id})")
        }
    }
}