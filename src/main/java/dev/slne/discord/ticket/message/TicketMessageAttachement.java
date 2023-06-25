package dev.slne.discord.ticket.message;

import java.util.Optional;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class TicketMessageAttachement {

    private Optional<Long> id;
    private TicketMessage message;

    private Optional<String> name;
    private Optional<String> url;
    private Optional<String> extension;
    private Optional<Integer> size;

    private Optional<String> description;

    /**
     * Constructor for a ticket message attachement
     *
     * @param clone The ticket message attachement to clone
     */
    public TicketMessageAttachement(TicketMessageAttachement clone) {
        this.id = clone.id;
        this.message = clone.message;

        this.name = clone.name;
        this.url = clone.url;
        this.extension = clone.extension;
        this.size = clone.size;
        this.description = clone.description;
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
        this.id = Optional.empty();
        this.message = message;

        this.name = Optional.ofNullable(name);
        this.url = Optional.ofNullable(url);
        this.extension = Optional.ofNullable(extension);
        this.size = Optional.ofNullable(size);
        this.description = Optional.ofNullable(description);
    }

    /**
     * Construct a new {@link TicketMessageAttachement}
     *
     * @param id          the id
     * @param message     the message
     * @param name        the name
     * @param url         the url
     * @param extension   the extension
     * @param size        the size
     * @param description the description
     */
    private TicketMessageAttachement(Optional<Long> id, TicketMessage message, Optional<String> name,
            Optional<String> url, Optional<String> extension, Optional<Integer> size, Optional<String> description) {
        this.id = id;
        this.message = message;
        this.name = name;
        this.url = url;
        this.extension = extension;
        this.size = size;
        this.description = description;
    }

    /**
     * Form a {@link TicketMessageAttachement} by the json object
     *
     * @param ticketMessage the ticket message
     * @param jsonObject    the json object
     * @return the {@link TicketMessageAttachement}
     */
    public static TicketMessageAttachement fromJsonObject(TicketMessage ticketMessage, JsonObject jsonObject) {
        Optional<Long> id = jsonObject.has("id") && jsonObject.get("id") != null && !(jsonObject
                .get("id") instanceof JsonNull) ? Optional.of(jsonObject.get("id").getAsLong()) : Optional.empty();
        Optional<String> name = jsonObject.has("name") && jsonObject.get("name") != null && !(jsonObject
                .get("name") instanceof JsonNull) ? Optional.of(jsonObject.get("name").getAsString())
                        : Optional.empty();
        Optional<String> url = jsonObject.has("url") && jsonObject.get("url") != null && !(jsonObject
                .get("url") instanceof JsonNull) ? Optional.of(jsonObject.get("url").getAsString())
                        : Optional.empty();
        Optional<String> extension = jsonObject.has("extension") && jsonObject.get("extension") != null && !(jsonObject
                .get("extension") instanceof JsonNull)
                        ? Optional.of(jsonObject.get("extension").getAsString())
                        : Optional.empty();
        Optional<Integer> size = jsonObject.has("size") && jsonObject.get("size") != null && !(jsonObject
                .get("size") instanceof JsonNull) ? Optional.of(jsonObject.get("size").getAsInt())
                        : Optional.empty();
        Optional<String> description = jsonObject.has("description") && jsonObject.get("description") != null
                && !(jsonObject
                        .get("description") instanceof JsonNull)
                                ? Optional.of(jsonObject.get("description").getAsString())
                                : Optional.empty();

        return new TicketMessageAttachement(id, ticketMessage, name, url, extension, size, description);
    }

    /**
     * Convert the {@link TicketMessageAttachement} to a json object
     *
     * @return the json object
     */
    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();

        id.ifPresent(value -> jsonObject.addProperty("id", value));
        name.ifPresent(value -> jsonObject.addProperty("name", value));
        url.ifPresent(value -> jsonObject.addProperty("url", value));
        extension.ifPresent(value -> jsonObject.addProperty("extension", value));
        size.ifPresent(value -> jsonObject.addProperty("size", value));
        description.ifPresent(value -> jsonObject.addProperty("description", value));

        return jsonObject;
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
    public Optional<String> getName() {
        return name;
    }

    /**
     * Get the url of the attachement
     *
     * @return The url of the attachement
     */
    public Optional<String> getUrl() {
        return url;
    }

    /**
     * Get the extension of the attachement
     *
     * @return The extension of the attachement
     */
    public Optional<String> getExtension() {
        return extension;
    }

    /**
     * Get the size of the attachement
     *
     * @return The size of the attachement
     */
    public Optional<Integer> getSize() {
        return size;
    }

    /**
     * Get the description of the attachement
     *
     * @return The description of the attachement
     */
    public Optional<String> getDescription() {
        return description;
    }

    /**
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

}
