package dev.slne.discord.instance;

import dev.slne.surf.surfapi.core.server.impl.SurfCoreApiImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.Internal
public class SurfDiscordApiImpl extends SurfCoreApiImpl<SurfDiscordPacketApiImpl> {

	public SurfDiscordApiImpl() {
		super(new SurfDiscordPacketApiImpl());
	}

	@Override
	public void sendPlayerToServer(UUID playerUuid, String server) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Optional<Object> getPlayer(@NotNull UUID playerUuid) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Path getDataFolder() {
		return Paths.get("data");
	}
}
