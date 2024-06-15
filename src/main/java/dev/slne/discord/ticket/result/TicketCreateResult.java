package dev.slne.discord.ticket.result;

/**
 * The enum Ticket create result.
 */
public enum TicketCreateResult {

	/**
	 * The ticket was created successfully
	 */
	SUCCESS,

	/**
	 * The ticket already exists
	 */
	ALREADY_EXISTS,

	/**
	 * The guild was not found
	 */
	GUILD_NOT_FOUND,

	/**
	 * The guild config was not found
	 */
	GUILD_CONFIG_NOT_FOUND,
	
	/**
	 * The category was not found
	 */
	CATEGORY_NOT_FOUND,

	/**
	 * The channel could not be created
	 */
	ERROR,

	/**
	 * The user is missing permissions
	 */
	MISSING_PERMISSIONS

}
