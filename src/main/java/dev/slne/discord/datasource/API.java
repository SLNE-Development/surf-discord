package dev.slne.discord.datasource;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

public class API {

    /**
     * Private constructor to prevent instantiation.
     */
    private API() {
        throw new IllegalStateException("Utility class");
    }

    private static final String ENVIRONMENT = "dev";

    private static final String API_VERSION = "v1";
    private static final String API_PREFIX = (ENVIRONMENT.equals("prod") ? "https://admin.slne.dev/api/"
            : "http://localhost:8000/api/") + API_VERSION + "/";

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

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * Converts a string to a slug
     *
     * @param input The input string
     * @return The slug
     */
    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
