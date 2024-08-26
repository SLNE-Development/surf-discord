package dev.slne.discord.spring.command;

import dev.slne.discord.spring.processor.DiscordCommandProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class UpdateGuildCommandsCommand {

  private final DiscordCommandProcessor discordCommandProcessor;
  private final JDA jda;

  @Autowired
  public UpdateGuildCommandsCommand(DiscordCommandProcessor discordCommandProcessor, JDA jda) {
    this.discordCommandProcessor = discordCommandProcessor;
    this.jda = jda;
  }

  @ShellMethod("Updates all commands for all guilds. This command should not be executed frequently as it can lead to rate restrictions.")
  public void updateGuildCommands() {
    for (final Guild guild : jda.getGuilds()) {
      discordCommandProcessor.updateCommands(guild);
    }
  }
}
