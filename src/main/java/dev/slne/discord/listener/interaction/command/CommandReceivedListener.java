package dev.slne.discord.listener.interaction.command;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.spring.processor.DiscordCommandProcessor;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Command received listener.
 */
@DiscordListener
public class CommandReceivedListener extends ListenerAdapter {

  private final DiscordCommandProcessor discordCommandProcessor;

  /**
   * Instantiates a new Command received listener.
   */
  @Autowired
  public CommandReceivedListener(DiscordCommandProcessor discordCommandProcessor) {
    this.discordCommandProcessor = discordCommandProcessor;
  }

  @Override
  public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
    discordCommandProcessor.getCommand(event.getName())
        .ifPresent(holder -> holder.command().execute(event));
  }
}
