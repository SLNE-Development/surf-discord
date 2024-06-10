package dev.slne.discord.ticket;

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
	DISCORD_SUPPORT("Discord Support"),
	/**
	 * The Server support.
	 */
	SERVER_SUPPORT("Server Support"),
	/**
	 * The Bugreport.
	 */
	BUGREPORT("Bug Report"),
	/**
	 * Whitelist ticket type.
	 */
	WHITELIST("Whitelist");

	@Nonnull
	private final String name;

	/**
	 * Creates a new {@link TicketType}.
	 *
	 * @param name The name of the ticket type.
	 */
	TicketType(@Nonnull String name) {
		this.name = name;
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
