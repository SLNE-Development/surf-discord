package dev.slne.discord.ticket;

import javax.annotation.Nonnull;

public enum TicketType {

    DISCORD_SUPPORT("Discord Support"),
    SERVER_SUPPORT("Server Support"),
    BUGREPORT("Bug Report"),
    WHITELIST("Whitelist");

    private @Nonnull String name;

    /**
     * Creates a new {@link TicketType}.
     *
     * @param name The name of the ticket type.
     */
    private TicketType(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Returns the ticket type by the given name.
     *
     * @param name The name of the ticket type.
     * @return The ticket type.
     */
    public static TicketType getByName(String name) {
        for (TicketType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    /**
     * Returns the name of the ticket type.
     *
     * @return The name of the ticket type.
     */
    public @Nonnull String getName() {
        return name;
    }

}
