package dev.slne.discord.listener;

import dev.slne.discord.listener.interaction.button.DiscordButtonListener;
import dev.slne.discord.listener.interaction.command.CommandReceivedListener;
import dev.slne.discord.listener.interaction.menu.DiscordSelectMenuListener;
import dev.slne.discord.listener.interaction.modal.DiscordModalListener;
import dev.slne.discord.listener.message.MessageCreatedListener;
import dev.slne.discord.listener.message.MessageDeletedListener;
import dev.slne.discord.listener.message.MessageUpdatedListener;
import dev.slne.discord.listener.reactionrole.ReactionRoleListener;
import dev.slne.discord.listener.redis.ticket.TicketCloseListener;
import dev.slne.discord.listener.whitelist.WhitelistJoinListener;
import dev.slne.discord.listener.whitelist.WhitelistQuitListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Listener manager.
 */
public class ListenerManager {

	private final List<Object> listeners;
	private final List<EventListener> discordListeners;

	/**
	 * Creates a new listener manager.
	 */
	public ListenerManager() {
		this.listeners = new ArrayList<>();
		this.discordListeners = new ArrayList<>();
	}

	/**
	 * Registers all listeners.
	 */
	public void registerListeners() {
		listeners.add(new TicketCloseListener());
	}

	/**
	 * Registers all discord listeners.
	 */
	public void registerDiscordListeners() {
		discordListeners.add(new WhitelistJoinListener());
		discordListeners.add(new WhitelistQuitListener());

		discordListeners.add(new CommandReceivedListener());
		discordListeners.add(new DiscordModalListener());
		discordListeners.add(new DiscordButtonListener());
		discordListeners.add(new DiscordSelectMenuListener());

		discordListeners.add(new MessageCreatedListener());
		discordListeners.add(new MessageUpdatedListener());
		discordListeners.add(new MessageDeletedListener());

		discordListeners.add(new ReactionRoleListener());
	}

	/**
	 * Registers a listener to the jda.
	 *
	 * @param jda the jda
	 */
	public void registerListenersToJda(JDA jda) {
		this.discordListeners.forEach(jda::addEventListener);
	}

}
