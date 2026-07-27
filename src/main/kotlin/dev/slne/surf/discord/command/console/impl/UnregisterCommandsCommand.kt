package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommand
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommandContext

object UnregisterCommandsCommand : MicroserviceCommand("unregistercommands") {
    override suspend fun MicroserviceCommandContext.execute(
        args: List<String>
    ) {
        CommandRegistrar.unregisterAllCommands()
    }
}