package dev.slne.discord.instance;

import dev.slne.surf.surfapi.core.api.SurfCoreApiAccess;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class SurfDiscordApiAccess extends SurfCoreApiAccess {

	/**
	 * Retrieves the instance of SurfVelocityApi.
	 *
	 * @return the SurfVelocityApi instance
	 */
	@ApiStatus.Internal
	public static SurfDiscordApiImpl getInstance() {
		return (SurfDiscordApiImpl) SurfCoreApiAccess.getInstance();
	}

	/**
	 * Sets the instance of the SurfVelocityApi.
	 * <p>
	 * This method sets the instance of the SurfVelocityApi by invoking the setInstance method of the
	 * SurfCoreApiAccess class. This allows accessing the SurfVelocityApi instance statically.
	 * <p>
	 * Example usage:
	 * {@snippet :
	 * SurfVelocityApiAccess.setInstance(api);
	 *}
	 *
	 * @param instance the SurfVelocityApi instance to set
	 */
	@ApiStatus.Internal
	public static void setInstance(SurfDiscordApiImpl instance) {
		SurfCoreApiAccess.setInstance(instance);
	}
}
