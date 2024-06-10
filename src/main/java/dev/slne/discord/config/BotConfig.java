package dev.slne.discord.config;

import dev.slne.discord.config.discord.DiscordBotConfig;
import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.role.RoleConfig;
import dev.slne.discord.config.ticket.TicketConfig;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Map;

/**
 * The interface Bot config.
 */
@ApiStatus.Internal
@ConfigSerializable
@ToString
@Getter
public class BotConfig {
	private static BotConfig instance;

	private Map<String, GuildConfig> guildConfig;
	private Map<String, RoleConfig> roleConfig;

	private DiscordBotConfig discordBotConfig;
	private TicketConfig ticketConfig;

	/**
	 * Instantiates a new Bot config.
	 */
	public BotConfig() {
	}

	/**
	 * Gets config.
	 *
	 * @return the config
	 */
	public static BotConfig getConfig() {
		if (instance != null) {
			return instance;
		}

		loadConfig();

		return instance;
	}

	/**
	 * Load config.
	 */
	public static void loadConfig() {
		YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(Path.of("data/config.yml")).build();

		try {
			CommentedConfigurationNode node = loader.load();

			if (node == null) {
				throw new IllegalStateException("Could not load configuration");
			}

			instance = node.get(BotConfig.class);
		} catch (Exception e) {
			System.err.println("An error occurred while loading this configuration: " + e.getMessage());

			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}
		}
	}

}
