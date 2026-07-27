package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommand
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommandContext

object InfoCommand : MicroserviceCommand("info") {
    override suspend fun MicroserviceCommandContext.execute(
        args: List<String>
    ) {
        logger.info("---- Guild Information ----")
        jda.guilds.forEach {
            logger.info("Connected to guild: ${it.name} (ID: ${it.id})")
        }
    }
}