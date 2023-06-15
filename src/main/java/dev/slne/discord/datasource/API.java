package dev.slne.discord.datasource;

public class API {

    /**
     * Private constructor to prevent instantiation.
     */
    private API() {
        throw new IllegalStateException("Utility class");
    }

    private static final String API_VERSION = "v1";
    private static final String API_PREFIX = "https://admin.slne.dev/api/" + API_VERSION + "/";

    public static final String TICKETS = API_PREFIX + "tickets";
    public static final String ACTIVE_TICKETS = API_PREFIX + "tickets/active";
    public static final String TICKET_CHANNEL_ID = API_PREFIX + "tickets/channel/%s";
    public static final String TICKET = API_PREFIX + "tickets/%s";
    public static final String TICKET_MESSAGES = API_PREFIX + "tickets/%s/messages";
    public static final String TICKET_MESSAGE = API_PREFIX + "tickets/%s/messages/%s";
    public static final String TICKET_MESSAGE_ATTACHMENTS = API_PREFIX + "tickets/%s/messages/%s/attachments";
    public static final String TICKET_MESSAGE_ATTACHMENT = API_PREFIX + "tickets/%s/messages/%s/attachments/%s";
    public static final String TICKET_MEMBERS = API_PREFIX + "tickets/%s/members";
    public static final String TICKET_MEMBER = API_PREFIX + "tickets/%s/members/%s";

}
