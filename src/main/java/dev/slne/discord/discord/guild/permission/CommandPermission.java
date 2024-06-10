package dev.slne.discord.discord.guild.permission;

import lombok.Getter;

/**
 * The enum Command permission.
 */
@Getter
public enum CommandPermission {

	NO_INTEREST("NO_INTEREST"),
	TWITCH_CONNECT("TWITCH_CONNECT"),
	TWITCH_FOLLOW("TWITCH_FOLLOW"),
	WHITELIST("WHITELIST"),
	WHITELISTED("WHITELISTED"),
	WHITELIST_QUERY("WHITELIST_QUERY"),
	WHITELIST_ROLE("WHITELIST_ROLE"),
	TICKET_ADD_USER("TICKET_ADD_USER"),
	TICKET_REMOVE_USER("TICKET_REMOVE_USER"),
	TICKET_CLOSE("TICKET_CLOSE"),
	TICKET_BUTTONS("TICKET_BUTTONS"),
	TICKET_INFO("TICKET_INFO"),
	TICKET_BAN("TICKET_BAN"),
	TICKET_UNBAN("TICKET_UNBAN"),
	TICKET_BANLIST("TICKET_BANLIST"),
	TICKET_BANINFO("TICKET_BANINFO"),
	TICKET_STATISTIC("TICKET_STATISTIC"),
	REACTION_ROLE_TEXT("REACTION_ROLE_TEXT");

	private final String name;

	/**
	 * Returns the name
	 *
	 * @param name The name.
	 */
	CommandPermission(String name) {
		this.name = name;
	}
}
