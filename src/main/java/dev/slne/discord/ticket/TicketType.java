package dev.slne.discord.ticket;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * The enum Ticket type.
 */
@Getter
public enum TicketType {

	WHITELIST(
			"Whitelist",
			"whitelist",
			"Whitelist Anfragen für den Survival Server",
			Emoji.fromUnicode("\uD83D\uDCDC"),
			DiscordPermission.VIEW_WHITELIST_TICKETS
	),

	SURVIVAL_SUPPORT(
			"Survival Support",
			"survival-support",
			"Anliegen bezüglich des Survival Servers",
			Emoji.fromUnicode("\uD83D\uDEE0\uFE0F"),
			DiscordPermission.VIEW_SURVIVAL_SUPPORT_TICKETS
	),

	EVENT_SUPPORT(
			"Event Support",
			"event-support",
			"Anliegen bezüglich des Event Servers",
			Emoji.fromUnicode("\uD83C\uDF89"),
			DiscordPermission.VIEW_EVENT_SUPPORT_TICKETS
	),

	BUGREPORT(
			"Bug Report",
			"bugreport",
			"Fehler gefunden? Melde ihn hier.",
			Emoji.fromUnicode("\uD83D\uDC1E"),
			DiscordPermission.VIEW_BUGREPORT_TICKETS
	),

	REPORT(
			"Report",
			"report",
			"Melde Griefing, Cheating oder andere Regelverstöße.",
			Emoji.fromUnicode("📢"),
			DiscordPermission.VIEW_REPORT_TICKETS
	),

	UNBAN(
			"Entbannungsantrag",
			"unban",
			"Entbannungsanträge für den Community Server",
			Emoji.fromUnicode("\uD83D\uDEAB"),
			DiscordPermission.VIEW_UNBAN_TICKETS
	),

	DISCORD_SUPPORT(
			"Discord Support",
					"discord-support",
					"Anliegen bezüglich des Discord Servers",
			Emoji.fromUnicode("\uD83D\uDCAC"),
	DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS
	);
	
	@Nonnull
	private final String name;
	@Nonnull
	private final String configName;
	@Nonnull
	private final String description;
	@Nonnull
	private final Emoji emoji;
	@Nonnull
	private final DiscordPermission viewPermission;

	/**
	 * Creates a new {@link TicketType}.
	 *
	 * @param name           The name of the ticket type.
	 * @param configName     the config name
	 * @param description    the description
	 * @param emoji          the emoji
	 * @param viewPermission the view permission
	 */
	TicketType(
			@Nonnull String name, @Nonnull String configName, @Nonnull String description,
			@Nonnull Emoji emoji, @Nonnull DiscordPermission viewPermission
	) {
		this.name = name;
		this.configName = configName;
		this.viewPermission = viewPermission;
		this.description = description;
		this.emoji = emoji;
	}

	/**
	 * Returns the ticket type by the given name.
	 *
	 * @param name The name of the ticket type.
	 *
	 * @return The ticket type.
	 */
	public static TicketType getByName(String name) {
		return Arrays.stream(values())
					 .filter(type -> type.getName().equalsIgnoreCase(name))
					 .findFirst()
					 .orElse(null);
	}

}
