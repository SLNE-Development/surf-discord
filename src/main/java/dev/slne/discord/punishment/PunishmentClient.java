package dev.slne.discord.punishment;

import dev.slne.discord.datasource.API;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The interface Punishment client.
 */
@FeignClient(name = "punishment-client", url = API.API_PREFIX)
public interface PunishmentClient {

	/**
	 * Gets ban by punishment id.
	 *
	 * @param punishment_id the punishment id
	 *
	 * @return the ban by punishment id
	 */
	@GetMapping(value = API.PUNISHMENT_BY_PUNISHMENT_ID)
	PunishmentBan getBanByPunishmentId(@PathVariable String punishment_id);

}
