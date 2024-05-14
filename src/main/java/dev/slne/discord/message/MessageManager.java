package dev.slne.discord.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * The type Message manager.
 */
public class MessageManager {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private MessageManager() {
	}

	/**
	 * Returns an error MessageEmbed with the given title and description.
	 *
	 * @param title       The title of the embed.
	 * @param description The description of the embed.
	 *
	 * @return The MessageEmbed.
	 */
	public static @Nonnull MessageEmbed getErrorEmbed(String title, String description) {
		EmbedBuilder embedBuilder = new EmbedBuilder();

		embedBuilder.setTitle(title);
		embedBuilder.setDescription(description);
		embedBuilder.setColor(0xff0000);
		embedBuilder.setTimestamp(Instant.now());

		return embedBuilder.build();
	}

}
