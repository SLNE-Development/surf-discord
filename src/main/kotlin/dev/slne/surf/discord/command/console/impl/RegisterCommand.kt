package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.microservice.api.microservice.command.microserviceCommand

fun registerCommand() = microserviceCommand("registercommands") {
    sendLine("Registering all Discord commands...")
    CommandRegistrar.registerAllCommands()
}