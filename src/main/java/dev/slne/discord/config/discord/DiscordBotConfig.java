package dev.slne.discord.config.discord;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

/**
 * The interface Discord bot config.
 */
@ConfHeader("Discord Bot Config")
public interface DiscordBotConfig {

	/**
	 * Bot token string.
	 *
	 * @return the string
	 */
	@AnnotationBasedSorter.Order(0)
	@ConfDefault.DefaultString("")
	String botToken();

}
