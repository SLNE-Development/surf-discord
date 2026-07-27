package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.logger
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommand
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommandContext

object RegisterCommand : MicroserviceCommand("registercommands") {
    override suspend fun MicroserviceCommandContext.execute(
        args: List<String>
    ) {
        logger.info("Registering all Discord commands...")
        CommandRegistrar.registerAllCommands()
    }
}