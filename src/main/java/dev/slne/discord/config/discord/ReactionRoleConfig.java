package dev.slne.discord.config.discord;

import dev.slne.discord.DiscordBot;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The interface Reaction role config.
 */
@Getter
@ConfigSerializable
@ToString
public class ReactionRoleConfig {

	private final String messageId = "0";
	private final String channelId = "0";
	private final String roleId = "0";
	private final String reaction = "🔔";

	/**
	 * Instantiates a new Reaction role config.
	 */
	private ReactionRoleConfig() {

	}

	/**
	 * Gets config.
	 *
	 * @param guildId the guild id
	 *
	 * @return the config
	 */
	static ReactionRoleConfig getConfig(String guildId) {
		return GuildConfig.getConfig(guildId).getReactionRole();
	}

	/**
	 * Gets role.
	 *
	 * @return the role
	 */
	public Role getRole() {
		return DiscordBot.getInstance().getJda().getRoleById(roleId);
	}

	/**
	 * Gets message rest.
	 *
	 * @return the message rest
	 */
	public RestAction<Message> getMessageRest() {
		TextChannel textChannel = DiscordBot.getInstance().getJda().getTextChannelById(channelId);

		if (textChannel == null) {
			return null;
		}

		return textChannel.retrieveMessageById(messageId);
	}

}
