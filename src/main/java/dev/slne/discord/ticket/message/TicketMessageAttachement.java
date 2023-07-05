package dev.slne.discord.ticket.message;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class TicketMessageAttachement {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("extension")
    private String extension;

    @SerializedName("size")
    private int size;

    @SerializedName("description")
    private String description;

    @SerializedName("message_id")
    private long messageId;

    /**
     * Constructor for a ticket message attachement
     *
     * @param clone The ticket message attachement to clone
     */
    public TicketMessageAttachement(TicketMessageAttachement clone) {
        this.id = clone.id;

        this.name = clone.name;
        this.url = clone.url;
        this.extension = clone.extension;
        this.size = clone.size;
        this.description = clone.description;

        this.messageId = clone.messageId;
    }

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
        this.id = 0;

        this.name = name;
        this.url = url;
        this.extension = extension;
        this.size = size;
        this.description = description;

        this.messageId = message.getId();
    }

    /**
     * Convert the {@link TicketMessageAttachement} to a json object
     *
     * @return the json object
     */
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", id);

        if (name != null) {
            jsonObject.addProperty("name", name);
        }

        if (url != null) {
            jsonObject.addProperty("url", url);
        }

        if (extension != null) {
            jsonObject.addProperty("extension", extension);
        }

        jsonObject.addProperty("size", size);

        if (description != null) {
            jsonObject.addProperty("description", description);
        }

        return jsonObject;
    }

    /**
     * Get the message the attachement is attached to
     *
     * @return The message the attachement is attached to
     */
    public TicketMessage getMessage() {
        if (messageId == 0) {
            return null;
        }

        return TicketMessage.getByMessageId(messageId);
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

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

}
