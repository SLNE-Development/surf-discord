package dev.slne.discord.datasource;

/**
 * The type Api.
 */
public class API {


	/**
	 * The constant API_PREFIX.
	 */
	public static final String API_PREFIX = "https://dapi.slne.dev/api/";

	/**
	 * The constant TICKETS.
	 */
	public static final String TICKETS = "ticket";
	/**
	 * The constant ACTIVE_TICKETS.
	 */
	public static final String ACTIVE_TICKETS = "ticket/active";
	/**
	 * The constant TICKET.
	 */
	public static final String TICKET = "ticket/{ticketId}";
	/**
	 * The constant TICKET_MESSAGES.
	 */
	public static final String TICKET_MESSAGES = "ticket/{ticketId}/messages";
	/**
	 * The constant TICKET_MEMBERS.
	 */
	public static final String TICKET_MEMBERS = "ticket/{ticketId}/member";
	/**
	 * The constant WHITELISTS.
	 */
	public static final String WHITELISTS = "whitelist";
	/**
	 * The constant WHITELIST.
	 */
	public static final String WHITELIST = "whitelist/%s";
	/**
	 * The constant WHITELIST_CHECK.
	 */
	public static final String WHITELIST_CHECK = "whitelist/check";
	/**
	 * The constant WHITELIST_BY_DISCORD_ID.
	 */
	public static final String WHITELIST_BY_DISCORD_ID = "whitelist/discord/%s";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private API() {
		throw new IllegalStateException("Utility class");
	}
}
