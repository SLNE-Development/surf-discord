package dev.slne.discord.datasource;

/**
 * The type Api.
 */
public class API {

	/**
	 * The constant LOCAL_API_PREFIX.
	 */
	public static final String LOCAL_API_PREFIX = "http://localhost:3000/api/";
	/**
	 * The constant GLOBAL_API_PREFIX.
	 */
	public static final String GLOBAL_API_PREFIX = "https://dapi.slne.dev/api/";
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
	 * The constant TICKET_MEMBER.
	 */
	public static final String TICKET_MEMBER = "ticket/{ticketId}/member/{memberId}";
	/**
	 * The constant WHITELISTS.
	 */
	public static final String WHITELISTS = "freebuild/whitelist";
	/**
	 * The constant WHITELIST.
	 */
	public static final String WHITELIST = "freebuild/whitelist/{uuid}";
	/**
	 * The constant WHITELIST_CHECK.
	 */
	public static final String WHITELIST_CHECK = "freebuild/whitelist/check";
	/**
	 * The constant WHITELIST_BY_DISCORD_ID.
	 */
	public static final String WHITELIST_BY_DISCORD_ID = "freebuild/whitelist/discord/{discordId}";
	/**
	 * The constant WHITELIST_BY_MINECRAFT_NAME.
	 */
	public static final String WHITELIST_BY_MINECRAFT_NAME = "proxy/user/uuid/{minecraftName}";


	public static final String PUNISHMENT_BY_PUNISHMENT_ID = "punish/bans/{punishment_id}";
	/**
	 * The constant API_PREFIX.
	 */
	public static final String API_PREFIX = GLOBAL_API_PREFIX;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private API() {
		throw new IllegalStateException("Utility class");
	}
}
