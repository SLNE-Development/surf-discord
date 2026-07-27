package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.ConsoleCommand

object UnregisterCommandsCommand : ConsoleCommand {
    override val name = "unregistercommands"

    override fun execute(args: List<String>) {
        CommandRegistrar.unregisterAllCommands()
    }
}