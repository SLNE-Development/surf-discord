package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.CommandRegistrar
import dev.slne.surf.discord.command.console.ConsoleCommand
import dev.slne.surf.discord.logger
import org.springframework.stereotype.Component

@Component
class RegisterCommand(
    private val commandRegistrar: CommandRegistrar
) : ConsoleCommand {
    override val name = "registercommands"

    override fun execute(args: List<String>) {
        logger.info("Registering all Discord commands...")
        commandRegistrar.registerAllCommands()
    }
}