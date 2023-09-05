package dev.slne.discord.datasource;

public class API {

    private static final String ENVIRONMENT = System.getProperty("environment", "prod");
    private static final String API_VERSION = "v1";
    private static final String API_PREFIX = (ENVIRONMENT.equals("prod") ? "https://admin.slne.dev/api/"
            : "http://localhost:8000/api/") + API_VERSION + "/";
    public static final String TICKETS = API_PREFIX + "tickets";
    public static final String ACTIVE_TICKETS = API_PREFIX + "tickets/active";
    public static final String TICKET = API_PREFIX + "tickets/%s";
    public static final String TICKET_MESSAGES = API_PREFIX + "tickets/%s/messages";
    public static final String TICKET_MEMBERS = API_PREFIX + "tickets/%s/members";
    public static final String TICKET_MEMBER = API_PREFIX + "tickets/%s/members/%s";
    public static final String WHITELISTS = API_PREFIX + "whitelist";
    public static final String WHITELIST = API_PREFIX + "whitelist/%s";
    public static final String WHITELIST_CHECK = API_PREFIX + "whitelist/check";
    public static final String WHITELIST_BY_DISCORD_ID = API_PREFIX + "whitelist/discord/%s";

    /**
     * Private constructor to prevent instantiation.
     */
    private API() {
        throw new IllegalStateException("Utility class");
    }
}
