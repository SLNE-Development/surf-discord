package dev.slne.discord.guild.permission;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;

/**
 * The enum Discord permission.
 */
@Getter
public enum
DiscordPermission {

	// General
	VIEW_CHANNEL("VIEW_CHANNEL", Permission.VIEW_CHANNEL),
	MANAGE_CHANNEL("MANAGE_CHANNEL", Permission.MANAGE_CHANNEL),

	// Messages
	SEND_MESSAGES("SEND_MESSAGES", Permission.MESSAGE_SEND),
	EMBED_LINKS("EMBED_LINKS", Permission.MESSAGE_EMBED_LINKS),
	ATTACH_FILES("ATTACH_FILES", Permission.MESSAGE_ATTACH_FILES),
	ADD_REACTIONS("ADD_REACTIONS", Permission.MESSAGE_ADD_REACTION),
	USE_EXTERNAL_EMOJIS("USE_EXTERNAL_EMOJIS", Permission.MESSAGE_EXT_EMOJI),
	USE_EXTERNAL_STICKERS("USE_EXTERNAL_STICKERS", Permission.MESSAGE_EXT_STICKER),
	MANAGE_MESSAGES("MANAGE_MESSAGES", Permission.MESSAGE_MANAGE),
	READ_MESSAGE_HISTORY("READ_MESSAGE_HISTORY", Permission.MESSAGE_HISTORY),
	USE_APPLICATION_COMMANDS("USE_APPLICATION_COMMANDS", Permission.USE_APPLICATION_COMMANDS),

	// Tickets
	VIEW_WHITELIST_TICKETS("VIEW_WHITELIST_TICKETS", null),
	VIEW_BUGREPORT_TICKETS("VIEW_BUGREPORT_TICKETS", null),

	VIEW_SURVIVAL_SUPPORT_TICKETS("VIEW_SURVIVAL_SUPPORT_TICKETS", null),
	VIEW_EVENT_SUPPORT_TICKETS("VIEW_EVENT_SUPPORT_TICKETS", null),
	VIEW_DISCORD_SUPPORT_TICKETS("VIEW_DISCORD_SUPPORT_TICKETS", null),
	VIEW_UNBAN_TICKETS("VIEW_UNBAN_TICKETS", null),
	VIEW_REPORT_TICKETS("VIEW_REPORT_TICKETS", null);

	private final String name;
	private final Permission permission;

	/**
	 * Returns the name
	 *
	 * @param name       The name.
	 * @param permission the permission
	 */
	DiscordPermission(String name, Permission permission) {
		this.name = name;
		this.permission = permission;
	}

}
