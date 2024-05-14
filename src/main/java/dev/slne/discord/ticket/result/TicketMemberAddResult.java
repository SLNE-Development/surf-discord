package dev.slne.discord.ticket.result;

/**
 * The enum Ticket member add result.
 */
public enum TicketMemberAddResult {

	/**
	 * The user was added successfully
	 */
	SUCCESS,

	/**
	 * The user was not found
	 */
	USER_NOT_FOUND,

	/**
	 * The user was already added
	 */
	USER_ALREADY_ADDED,

	/**
	 * There was an error while adding the user
	 */
	ERROR

}
