package dev.slne.discord.discord.guild.role.roles;

import dev.slne.discord.discord.guild.permission.DiscordPermission;
import dev.slne.discord.discord.guild.role.DiscordRole;

import java.util.List;

/**
 * The type Server admin discord role.
 */
public class ServerAdminDiscordRole extends DiscordRole {

	/**
	 * Create a new server admin role with the name "ServerAdmin" and the following
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
	 * <li>MANAGE_ROLES</li>
	 * <li>MANAGE_WEBHOOKS</li>
	 * <li>VIEW_DISCORD_SUPPORT_TICKETS</li>
	 * <li>VIEW_SERVER_SUPPORT_TICKETS</li>
	 * <li>VIEW_BUGREPORT_TICKETS</li>
	 * <li>VIEW_WHITELIST_TICKETS</li>
	 * <li>USE_COMMAND_NO_INTEREST</li>
	 * <li>USE_COMMAND_TICKET_ADD_USER</li>
	 * <li>USE_COMMAND_TICKET_BAN</li>
	 * <li>USE_COMMAND_TICKET_BANLIST</li>
	 * <li>USE_COMMAND_TICKET_CLOSE</li>
	 * <li>USE_COMMAND_TICKET_INFO</li>
	 * <li>USE_COMMAND_TICKET_REMOVE_USER</li>
	 * <li>USE_COMMAND_TICKET_UNBAN</li>
	 * <li>USE_COMMAND_TWITCH_CONNECT</li>
	 * <li>USE_COMMAND_WHITELIST</li>
	 * <li>USE_COMMAND_WHITELISTED</li>
	 * <li>USE_COMMAND_WHITELIST_QUERY</li>
	 * <li>USE_COMMAND_WHITELIST_RANK</li>
	 * <li>USE_COMMAND_REACTION_ROLE_TEXT</li>
	 * <li>USE_COMMAND_TICKET_BANINFO</li>
	 * <li>USE_COMMAND_TICKET_STATISTIC</li>
	 * </ul>
	 */
	public ServerAdminDiscordRole() {
		super(DiscordRole.SERVER_ADMIN_ROLE, List.of(
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
				DiscordPermission.MANAGE_WEBHOOKS,
				DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS,
				DiscordPermission.VIEW_SERVER_SUPPORT_TICKETS,
				DiscordPermission.VIEW_BUGREPORT_TICKETS,
				DiscordPermission.VIEW_WHITELIST_TICKETS,
				DiscordPermission.USE_COMMAND_NO_INTEREST,
				DiscordPermission.USE_COMMAND_TICKET_ADD_USER,
				DiscordPermission.USE_COMMAND_TICKET_BAN,
				DiscordPermission.USE_COMMAND_TICKET_BUTTONS,
				DiscordPermission.USE_COMMAND_TICKET_BANLIST,
				DiscordPermission.USE_COMMAND_TICKET_CLOSE,
				DiscordPermission.USE_COMMAND_TICKET_INFO,
				DiscordPermission.USE_COMMAND_TICKET_REMOVE_USER,
				DiscordPermission.USE_COMMAND_TICKET_UNBAN,
				DiscordPermission.USE_COMMAND_TWITCH_CONNECT,
				DiscordPermission.USE_COMMAND_WHITELIST,
				DiscordPermission.USE_COMMAND_WHITELISTED,
				DiscordPermission.USE_COMMAND_WHITELIST_QUERY,
				DiscordPermission.USE_COMMAND_WHITELIST_ROLE,
				DiscordPermission.USE_COMMAND_REACTION_ROLE_TEXT,
				DiscordPermission.USE_COMMAND_TICKET_BANINFO,
				DiscordPermission.USE_COMMAND_TICKET_STATISTIC
		));
	}

}
