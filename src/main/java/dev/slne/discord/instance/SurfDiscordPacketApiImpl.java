package dev.slne.discord.instance;

import dev.slne.surf.surfapi.core.api.packet.SurfCorePacketEntityApi;
import dev.slne.surf.surfapi.core.server.impl.packet.SurfCorePacketApiImpl;
import org.jetbrains.annotations.ApiStatus;

/**
 * The type Surf discord packet api.
 */
@ApiStatus.Internal
public class SurfDiscordPacketApiImpl extends SurfCorePacketApiImpl {

	@Override
	public SurfCorePacketEntityApi getPacketEntityApi() {
		throw new UnsupportedOperationException("Not implemented.");
	}
}
