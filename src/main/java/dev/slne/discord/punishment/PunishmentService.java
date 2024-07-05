package dev.slne.discord.punishment;

import dev.slne.discord.Launcher;
import org.springframework.data.util.Lazy;

import java.util.concurrent.CompletableFuture;

public class PunishmentService {

	/**
	 * The constant INSTANCE.
	 */
	public static final PunishmentService INSTANCE = new PunishmentService();

	private final Lazy<PunishmentClient> punishmentClient =
			Lazy.of(() -> Launcher.getContext().getBean(PunishmentClient.class));

	/**
	 * Gets ban by punishment id.
	 *
	 * @param punishment_id the punishment id
	 *
	 * @return the ban by punishment id
	 */
	public CompletableFuture<PunishmentBan> getBanByPunishmentId(String punishment_id) {
		return CompletableFuture.supplyAsync(() -> punishmentClient.get().getBanByPunishmentId(punishment_id));
	}
}
