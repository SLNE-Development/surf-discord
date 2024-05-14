package dev.slne.discord.config;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.config.ConfigManager;
import org.jetbrains.annotations.ApiStatus;

/**
 * The type Config util.
 */
@ApiStatus.Internal
public class ConfigUtil {

	/**
	 * The constant FILE_NAME.
	 */
	public static final String FILE_NAME = "config.yml";

	/**
	 * Gets config.
	 *
	 * @return the config
	 */
	public static BotConfig getConfig() {
		return ConfigUtil.Holder.INSTANCE.getConfig();
	}

	/**
	 * The type Holder.
	 */
	static class Holder {
		/**
		 * The constant INSTANCE.
		 */
		public static final ConfigUtil.Holder INSTANCE = new ConfigUtil.Holder();

		private final ConfigManager<BotConfig> configManager;
		private boolean loaded = false;

		private Holder() {
			configManager =
					ConfigManager.create(DataApi.getDataInstance().getDataPath(), FILE_NAME, BotConfig.class);
		}

		/**
		 * Gets config.
		 *
		 * @return the config
		 */
		public BotConfig getConfig() {
			if (!loaded) {
				configManager.reloadConfig();
				loaded = true;
			}

			return configManager.getConfigData();
		}
	}
}
