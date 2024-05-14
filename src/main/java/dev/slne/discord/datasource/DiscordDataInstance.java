package dev.slne.discord.datasource;

import dev.slne.data.core.instance.CoreDataInstance;

import java.nio.file.Path;

/**
 * The type Discord data instance.
 */
public class DiscordDataInstance extends CoreDataInstance {

	@Override
	public ClassLoader getDataClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public Path getDataPath() {
		return Path.of("data");
	}

	@Override
	public String getServerName() {
		return "Discord";
	}

	@Override
	public String getIpAddress() {
		return "127.0.0.1";
	}

	@Override
	public int getPort() {
		return 0;
	}
}
