package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.logger

object RegisterCommand : ConsoleCommand {
    override val name = "registercommands"

    override fun execute(args: List<String>) {
        logger.info("Registering all Discord commands...")
        CommandRegistrar.registerAllCommands()
    }
}