package dev.slne.discord.whitelist;

import dev.slne.discord.datasource.API;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

/**
 * The interface Whitelist client.
 */
@FeignClient(name = "whitelist-client", url = API.API_PREFIX)
public interface WhitelistClient {

	/**
	 * Gets whitelist by uuid.
	 *
	 * @param uuid the uuid
	 *
	 * @return the whitelist by uuid
	 */
	@GetMapping(value = API.WHITELIST)
	Whitelist getWhitelistByUuid(@PathVariable UUID uuid);

	/**
	 * Check whitelists list.
	 *
	 * @param request the request
	 *
	 * @return the list
	 */
	@PostMapping(value = API.WHITELIST_CHECK)
	List<Whitelist> checkWhitelists(WhitelistCheckPostRequest request);

	/**
	 * Gets whitelist by discord id.
	 *
	 * @param discordId the discord id
	 *
	 * @return the whitelist by discord id
	 */
	@GetMapping(value = API.WHITELIST_BY_DISCORD_ID)
	Whitelist getWhitelistByDiscordId(@PathVariable String discordId);

	/**
	 * Sync add whitelist whitelist.
	 *
	 * @param whitelist the whitelist
	 *
	 * @return the whitelist
	 */
	@PostMapping(value = API.WHITELISTS)
	Whitelist addWhitelist(Whitelist whitelist);

	/**
	 * Sync update whitelist whitelist.
	 *
	 * @param uuid      the uuid
	 * @param whitelist the whitelist
	 *
	 * @return the whitelist
	 */
	@PutMapping(value = API.WHITELIST)
	Whitelist updateWhitelist(@PathVariable UUID uuid, Whitelist whitelist);

	/**
	 * Gets uuid by minecraft name.
	 *
	 * @param minecraftName the minecraft name
	 *
	 * @return the uuid by minecraft name
	 */
	@GetMapping(value = API.WHITELIST_BY_MINECRAFT_NAME)
	ProxyUuidByMinecraftName getUuidByMinecraftName(@PathVariable String minecraftName);

	/**
	 * Instantiates a new Whitelist check post request.
	 *
	 * @param uuid       the uuid
	 * @param discordId  the discord id
	 * @param twitchLink the twitch link
	 */
	record WhitelistCheckPostRequest(UUID uuid, String discordId, String twitchLink) {
	}

	/**
	 * The type Proxy uuid by minecraft name.
	 */
	@Getter
	@NoArgsConstructor
	class ProxyUuidByMinecraftName {
		/**
		 * -- GETTER --
		 * Gets uuid.
		 *
		 * @return the uuid
		 */
		private UUID uuid;

		/**
		 * Instantiates a new Proxy uuid by minecraft name.
		 *
		 * @param uuid the uuid
		 */
		public ProxyUuidByMinecraftName(UUID uuid) {
			this.uuid = uuid;
		}

	}
}
