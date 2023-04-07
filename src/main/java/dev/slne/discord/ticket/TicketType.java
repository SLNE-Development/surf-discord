package dev.slne.discord.ticket;

public enum TicketType {

    DISCORD_SUPPORT("Discord Support"),
    SERVER_SUPPORT("Server Support"),
    BUGREPORT("Bug Report"),
    WHITELIST("Whitelist");

    private String name;

    private TicketType(String name) {
        this.name = name;
    }

    public static TicketType getByName(String name) {
        for (TicketType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

}
