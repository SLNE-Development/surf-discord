package dev.slne.discord.console

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.slne.discord.console.commands.ExitCommand
import dev.slne.discord.console.commands.ReRegisterBotCommands
import dev.slne.discord.console.commands.ReRegisterContextMenuCommands
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import dev.slne.discord.discord.interaction.context.DiscordContextMenuCommandProcessor
import net.dv8tion.jda.api.JDA

object MainDiscordCommand : SuspendingCliktCommand(">") {
    override suspend fun run() = Unit
}

fun buildRootCommand(
    jda: JDA,
    commandProcessor: DiscordCommandProcessor,
    contextMenuCommandProcessor: DiscordContextMenuCommandProcessor
) =
    MainDiscordCommand.subcommands(
        ExitCommand,
        ReRegisterBotCommands(jda, commandProcessor),
        ReRegisterContextMenuCommands(jda, contextMenuCommandProcessor)
    )