package dev.slne.discord.spring.command

import dev.slne.discord.spring.processor.DiscordCommandManager
import net.dv8tion.jda.api.JDA
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class UpdateGuildCommandsCommand @Autowired constructor(
    private val discordCommandProcessor: DiscordCommandManager,
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
