package dev.slne.discord.config.discord;

import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The interface Discord bot config.
 */
@ConfigSerializable
@Getter
@ToString
public class DiscordBotConfig {

	private String botToken;

	/**
	 * Instantiates a new Discord bot config.
	 */
	public DiscordBotConfig() {
	}

}
