package dev.slne.discord.discord.guild.permission;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;

/**
 * The enum Discord permission.
 */
@Getter
public enum DiscordPermission {

	// General
	VIEW_CHANNEL("VIEW_CHANNEL", Permission.VIEW_CHANNEL),
	MANAGE_CHANNEL("MANAGE_CHANNEL", Permission.MANAGE_CHANNEL),

	// Threads
	CREATE_PUBLIC_THREADS("CREATE_PUBLIC_THREADS", Permission.CREATE_PUBLIC_THREADS),
	CREATE_PRIVATE_THREADS("CREATE_PRIVATE_THREADS", Permission.CREATE_PRIVATE_THREADS),
	SEND_MESSAGES_IN_THREADS("SEND_MESSAGES_IN_THREADS", Permission.MESSAGE_SEND_IN_THREADS),
	MANAGE_THREADS("MANAGE_THREADS", Permission.MANAGE_THREADS),

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

	// Webhooks
	MANAGE_WEBHOOKS("MANAGE_WEBHOOKS", Permission.MANAGE_WEBHOOKS),

	// Tickets
	VIEW_WHITELIST_TICKETS("VIEW_WHITELIST_TICKETS", null),
	VIEW_SERVER_SUPPORT_TICKETS("VIEW_SERVER_SUPPORT_TICKETS", null),
	VIEW_DISCORD_SUPPORT_TICKETS("VIEW_DISCORD_SUPPORT_TICKETS", null),
	VIEW_BUGREPORT_TICKETS("VIEW_BUGREPORT_TICKETS", null);
	
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
