package dev.slne.discord.config.ticket;

import dev.slne.discord.config.BotConfig;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

/**
 * The interface Ticket config.
 */
@ConfigSerializable
@Getter
@ToString
public class TicketConfig {

	private boolean enabled;
	private Map<String, TicketTypeConfig> ticketTypes;

	private TicketConfig() {
	}

	/**
	 * Gets config.
	 *
	 * @return the config
	 */
	public static TicketConfig getConfig() {
		return BotConfig.getConfig().getTicketConfig();
	}

}
