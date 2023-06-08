package dev.slne.discord.ticket.message;

public class TicketMessageAttachement {

    private TicketMessage message;

    private String name;
    private String url;
    private String extension;
    private int size;

    private String description;

    /**
     * Constructor for a ticket message attachement
     *
     * @param message     The message the attachement is attached to
     * @param name        The name of the attachement
     * @param url         The url of the attachement
     * @param extension   The extension of the attachement
     * @param size        The size of the attachement
     * @param description The description of the attachement
     */
    public TicketMessageAttachement(TicketMessage message, String name, String url, String extension, int size,
            String description) {
        this.message = message;

        this.name = name;
        this.url = url;
        this.extension = extension;
        this.size = size;
        this.description = description;
    }

    /**
     * Get the message the attachement is attached to
     *
     * @return The message the attachement is attached to
     */
    public TicketMessage getMessage() {
        return message;
    }

    /**
     * Get the name of the attachement
     *
     * @return The name of the attachement
     */
    public String getName() {
        return name;
    }

    /**
     * Get the url of the attachement
     *
     * @return The url of the attachement
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the extension of the attachement
     *
     * @return The extension of the attachement
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Get the size of the attachement
     *
     * @return The size of the attachement
     */
    public int getSize() {
        return size;
    }

    /**
     * Get the description of the attachement
     *
     * @return The description of the attachement
     */
    public String getDescription() {
        return description;
    }

}
