package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.microservice.api.microservice.command.microserviceCommand

fun unregisterCommandsCommand() = microserviceCommand("unregistercommands") {
    sendLine("Unregistering all Discord commands...")
    CommandRegistrar.unregisterAllCommands()
}