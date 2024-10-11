package dev.slne.discord.spring.command

import dev.slne.discord.spring.processor.DiscordCommandProcessor
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class UpdateGuildCommandsCommand @Autowired constructor(
    private val discordCommandProcessor: DiscordCommandProcessor,
    jda: JDA
) {
    private val jda: JDA = jda

    @ShellMethod("Updates all commands for all guilds. This command should not be executed frequently as it can lead to rate restrictions.")
    fun updateGuildCommands() {
        for (guild in jda.getGuilds()) {
            discordCommandProcessor.updateCommands(guild)
        }
    }
}
