package dev.slne.discord.config;

import dev.slne.discord.config.discord.DiscordBotConfig;
import dev.slne.discord.config.ticket.TicketConfig;
import org.jetbrains.annotations.ApiStatus;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

/**
 * The interface Bot config.
 */
@ApiStatus.Internal
public interface BotConfig {

	/**
	 * Discord bot config discord bot config.
	 *
	 * @return the discord bot config
	 */
	@SubSection
	@ConfKey("bot")
	DiscordBotConfig discordBotConfig();

	/**
	 * Ticket config ticket config.
	 *
	 * @return the ticket config
	 */
	@SubSection
	@ConfKey("ticket")
	TicketConfig ticketConfig();
}
