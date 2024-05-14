package dev.slne.discord.discord.guild.role.roles;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.guild.role.DiscordRole;

import java.util.List;

/**
 * The type Discord mod discord role.
 */
public class DiscordModDiscordRole extends DiscordRole {

	/**
	 * Create a new discord mod role with the name "DiscordMod" and the following
	 * permissions:
	 *
	 * <ul>
	 * <li>VIEW_CHANNEL</li>
	 * <li>MANAGE_CHANNEL</li>
	 * <li>CREATE_PUBLIC_THREADS</li>
	 * <li>CREATE_PRIVATE_THREADS</li>
	 * <li>SEND_MESSAGES_IN_THREADS</li>
	 * <li>MANAGE_THREADS</li>
	 * <li>SEND_MESSAGES</li>
	 * <li>EMBED_LINKS</li>
	 * <li>ATTACH_FILES</li>
	 * <li>ADD_REACTIONS</li>
	 * <li>USE_EXTERNAL_EMOJIS</li>
	 * <li>USE_EXTERNAL_STICKERS</li>
	 * <li>MANAGE_MESSAGES</li>
	 * <li>READ_MESSAGE_HISTORY</li>
	 * <li>SEND_TTS_MESSAGES</li>
	 * <li>USE_APPLICATION_COMMANDS</li>
	 * <li>ALL_ROLE</li>
	 * </ul>
	 */
	public DiscordModDiscordRole() {
		super(DiscordRole.DISCORD_MOD_ROLE, List.of(
				DiscordPermission.VIEW_CHANNEL,
				DiscordPermission.MANAGE_CHANNEL,
				DiscordPermission.CREATE_PUBLIC_THREADS,
				DiscordPermission.CREATE_PRIVATE_THREADS,
				DiscordPermission.SEND_MESSAGES_IN_THREADS,
				DiscordPermission.MANAGE_THREADS,
				DiscordPermission.SEND_MESSAGES,
				DiscordPermission.EMBED_LINKS,
				DiscordPermission.ATTACH_FILES,
				DiscordPermission.ADD_REACTIONS,
				DiscordPermission.USE_EXTERNAL_EMOJIS,
				DiscordPermission.USE_EXTERNAL_STICKERS,
				DiscordPermission.MANAGE_MESSAGES,
				DiscordPermission.READ_MESSAGE_HISTORY,
				DiscordPermission.USE_APPLICATION_COMMANDS,
				DiscordPermission.ALL_ROLE
		));
	}

}
