package dev.slne.discord.whitelist;

import dev.slne.discord.Launcher;
import org.springframework.data.util.Lazy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Whitelist service.
 */
public class WhitelistService {

	/**
	 * The constant INSTANCE.
	 */
	public static final WhitelistService INSTANCE = new WhitelistService();

	private final Lazy<WhitelistClient> whitelistClient =
			Lazy.of(() -> Launcher.getContext().getBean(WhitelistClient.class));

	/**
	 * Update whitelist completable future.
	 *
	 * @param whitelist the whitelist
	 *
	 * @return the completable future
	 */
	public CompletableFuture<Whitelist> updateWhitelist(Whitelist whitelist) {
		return CompletableFuture.supplyAsync(
				() -> whitelistClient.get().updateWhitelist(whitelist.getUuid(), whitelist));
	}

	/**
	 * Gets whitelist by uuid.
	 *
	 * @param uuid the uuid
	 *
	 * @return the whitelist by uuid
	 */
	public CompletableFuture<Whitelist> getWhitelistByUuid(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> whitelistClient.get().getWhitelistByUuid(uuid));
	}

	/**
	 * Add whitelist completable future.
	 *
	 * @param whitelist the whitelist
	 *
	 * @return the completable future
	 */
	public CompletableFuture<Whitelist> addWhitelist(Whitelist whitelist) {
		return CompletableFuture.supplyAsync(() -> whitelistClient.get().addWhitelist(whitelist));
	}

	/**
	 * Gets whitelist by discord id.
	 *
	 * @param discordId the discord id
	 *
	 * @return the whitelist by discord id
	 */
	public CompletableFuture<Whitelist> getWhitelistByDiscordId(String discordId) {
		return CompletableFuture.supplyAsync(() -> whitelistClient.get().getWhitelistByDiscordId(discordId));
	}

	/**
	 * Check whitelists completable future.
	 *
	 * @param uuid       the uuid
	 * @param discordId  the discord id
	 * @param twitchLink the twitch link
	 *
	 * @return the completable future
	 */
	public CompletableFuture<List<Whitelist>> checkWhitelists(UUID uuid, String discordId, String twitchLink) {
		return CompletableFuture.supplyAsync(
				() -> whitelistClient.get().checkWhitelists(new WhitelistClient.WhitelistCheckPostRequest(uuid,
																										  discordId,
																										  twitchLink
				)));
	}

}
