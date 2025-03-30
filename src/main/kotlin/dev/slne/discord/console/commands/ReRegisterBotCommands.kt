package dev.slne.discord.console.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.theme
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.utils.MiscUtil

class ReRegisterBotCommands(
    private val jda: JDA,
    private val commandProcessor: DiscordCommandProcessor,
) : SuspendingCliktCommand("reregister") {

    private val guildId by option().validate {
        try {
            MiscUtil.parseSnowflake(it)
        } catch (e: NumberFormatException) {
            fail(e.message ?: "???")
        }
    }

    override fun help(context: Context) = buildString {
        currentContext.theme.apply {
            appendLine(info("Reregister bot commands for a guild or all guilds."))
            appendLine(info("If no guild ID is provided, all guilds will be updated."))
            appendLine(info("If a guild ID is provided, only that guild will be updated."))
        }
    }

    override suspend fun run() {
        echo(currentContext.theme.info("Reregistering bot commands..."))
        val guildId = guildId

        if (guildId != null) {
            val guild = jda.getGuildById(guildId)

            if (guild == null) {
                echo(currentContext.theme.danger("Guild with ID $guildId not found."))
            } else {
                updateCommands(guild)
            }
        } else {
            for (guild in jda.guilds) {
                updateCommands(guild)
            }
        }
    }

    private suspend fun updateCommands(guild: Guild) {
        echo(currentContext.theme.info("Reregistering commands for guild: ${guild.name}"))

        commandProcessor.updateCommands(guild)
    }
}