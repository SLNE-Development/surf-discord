package dev.slne.discord.ticket;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * The enum Ticket type.
 */
@Getter
public enum TicketType {

	/**
	 * The Discord support.
	 */
	DISCORD_SUPPORT("Discord Support", "discord-support", DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS),
	/**
	 * The Server support.
	 */
	SERVER_SUPPORT("Server Support", "server-support", DiscordPermission.VIEW_SERVER_SUPPORT_TICKETS),
	/**
	 * The Bugreport.
	 */
	BUGREPORT("Bug Report", "bugreport", DiscordPermission.VIEW_BUGREPORT_TICKETS),
	/**
	 * Whitelist ticket type.
	 */
	WHITELIST("Whitelist", "whitelist", DiscordPermission.VIEW_WHITELIST_TICKETS);

	@Nonnull
	private final String name;

	@Nonnull
	private final String configName;

	private final DiscordPermission viewPermission;

	/**
	 * Creates a new {@link TicketType}.
	 *
	 * @param name           The name of the ticket type.
	 * @param configName     the config name
	 * @param viewPermission the view permission
	 */
	TicketType(@Nonnull String name, @Nonnull String configName, DiscordPermission viewPermission) {
		this.name = name;
		this.configName = configName;
		this.viewPermission = viewPermission;
	}

	/**
	 * Returns the ticket type by the given name.
	 *
	 * @param name The name of the ticket type.
	 *
	 * @return The ticket type.
	 */
	public static TicketType getByName(String name) {
		return Arrays.stream(values())
					 .filter(type -> type.getName().equalsIgnoreCase(name))
					 .findFirst()
					 .orElse(null);
	}

}
