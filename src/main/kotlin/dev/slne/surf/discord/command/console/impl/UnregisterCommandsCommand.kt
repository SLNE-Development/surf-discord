package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.ConsoleCommand
import org.springframework.stereotype.Component

@Component
class UnregisterCommandsCommand(
    private val commandRegistrar: CommandRegistrar
) : ConsoleCommand {
    override val name = "unregistercommands"

    override fun execute(args: List<String>) {
        commandRegistrar.unregisterAllCommands()
    }
}