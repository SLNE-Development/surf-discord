package dev.slne.discord.listener.interaction.command;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.discord.interaction.command.DiscordCommand;
import dev.slne.discord.discord.interaction.command.DiscordCommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import javax.annotation.Nonnull;

/**
 * The type Command received listener.
 */
public class CommandReceivedListener extends ListenerAdapter {

	private final DiscordCommandManager commandManager;

	/**
	 * Instantiates a new Command received listener.
	 */
	public CommandReceivedListener() {
		this.commandManager = DiscordBot.getInstance().getCommandManager();
	}

	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
		SlashCommandInteraction interaction = event.getInteraction();
		DiscordCommand command = commandManager.findCommand(interaction.getName());

		if (command != null) {
			command.internalExecute(event);
		}
	}

}
