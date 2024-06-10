package dev.slne.discord.config.ticket;

import dev.slne.discord.ticket.TicketType;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 * The type Ticket type config.
 */
@Getter
@ConfigSerializable
@ToString
public class TicketTypeConfig {

	private boolean enabled;
	private boolean shouldPrintWlQuery;
	private List<String> openingMessages;

	/**
	 * Instantiates a new Ticket type config.
	 */
	private TicketTypeConfig() {
	}

	/**
	 * Gets config.
	 *
	 * @param ticketType the ticket type
	 *
	 * @return the config
	 */
	public static TicketTypeConfig getConfig(String ticketType) {
		return TicketConfig.getConfig().getTicketTypes().get(ticketType);
	}

	/**
	 * Gets config.
	 *
	 * @param ticketType the ticket type
	 *
	 * @return the config
	 */
	public static TicketTypeConfig getConfig(TicketType ticketType) {
		return getConfig(ticketType.getConfigName());
	}
}
