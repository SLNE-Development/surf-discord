package dev.slne.discordold.console

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.slne.discordold.console.commands.ExitCommand
import dev.slne.discordold.console.commands.ReRegisterBotCommands
import dev.slne.discordold.discord.interaction.command.DiscordCommandProcessor
import net.dv8tion.jda.api.JDA

object MainDiscordCommand : SuspendingCliktCommand(">") {
    override suspend fun run() = Unit
}

fun buildRootCommand(
    jda: JDA,
    commandProcessor: DiscordCommandProcessor,
) = MainDiscordCommand.subcommands(
    ExitCommand,
    ReRegisterBotCommands(jda, commandProcessor),
)