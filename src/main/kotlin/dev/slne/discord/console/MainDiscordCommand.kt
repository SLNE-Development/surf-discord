package dev.slne.discord.console

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.slne.discord.console.commands.ExitCommand
import dev.slne.discord.console.commands.ReRegisterBotCommands

object MainDiscordCommand : SuspendingCliktCommand(">") {
    override suspend fun run() = Unit
}

fun buildRootCommand() = MainDiscordCommand.subcommands(
    ExitCommand,
    ReRegisterBotCommands
)