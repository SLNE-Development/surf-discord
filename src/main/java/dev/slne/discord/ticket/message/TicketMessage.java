package dev.slne.discord.ticket.message;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.data.core.web.WebResponse;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class TicketMessage {

    private Optional<Long> id;
    private Ticket ticket;
    private Optional<Message> message;
    private String messageId;

    private String authorId;
    private User author;

    private LocalDateTime messageCreatedAt;
    private Optional<LocalDateTime> messageEditedAt;
    private Optional<LocalDateTime> messageDeletedAt;

    private Optional<String> referencesMessageId;
    private Optional<Message> referencesMessage;

    private List<TicketMessageAttachement> attachments;
    private boolean botMessage;

    /**
     * Constructor for a ticket message
     *
     * @param clone The ticket message to clone
     */
    public TicketMessage(TicketMessage clone) {
        this.id = clone.id;
        this.ticket = clone.ticket;
        this.message = clone.message;
        this.messageId = clone.messageId;

        this.authorId = clone.authorId;
        this.author = clone.author;

        this.messageCreatedAt = clone.messageCreatedAt;
        this.messageEditedAt = clone.messageEditedAt;
        this.messageDeletedAt = clone.messageDeletedAt;

        this.referencesMessageId = clone.referencesMessageId;
        this.referencesMessage = clone.referencesMessage;

        this.attachments = new ArrayList<>(clone.attachments);
        this.botMessage = clone.botMessage;
    }

    /**
     * Constructor for a ticket message
     *
     * @param message The message to create the ticket message from
     */
    public TicketMessage(Ticket ticket, Message message) {
        this.id = Optional.empty();
        this.ticket = ticket;
        this.message = Optional.of(message);
        this.messageId = message.getId();

        this.author = message.getAuthor();
        this.authorId = this.author.getId();

        this.messageCreatedAt = message.getTimeCreated().toLocalDateTime();

        OffsetDateTime timeEdited = message.getTimeEdited();
        this.messageEditedAt = Optional.ofNullable(
                message.isEdited() && timeEdited != null ? timeEdited.toLocalDateTime() : null);

        this.messageDeletedAt = Optional.empty();

        MessageReference reference = message.getMessageReference();
        this.referencesMessage = Optional.empty();
        this.referencesMessageId = Optional.empty();
        if (reference != null) {
            reference.resolve().queue(referencedMessage -> this.referencesMessageId = Optional.of(
                    referencedMessage.getId()));

            this.referencesMessage = Optional.ofNullable(reference.getMessage());
            this.referencesMessageId = Optional.ofNullable(reference.getMessageId());
        }

        this.attachments = new ArrayList<>();
        for (Attachment attachment : message.getAttachments()) {
            String name = attachment.getFileName();
            String url = attachment.getUrl();
            String extension = attachment.getFileExtension();
            int size = attachment.getSize();

            String description = attachment.getDescription();

            TicketMessageAttachement attachement = new TicketMessageAttachement(this, name, url, extension, size,
                    description);
            this.attachments.add(attachement);
        }

        this.botMessage = message.getAuthor().isBot();
    }

    /**
     * Constructs a new {@link TicketMessage}
     *
     * @param id                  the id
     * @param ticket              the {@link Ticket}
     * @param message             the {@link Message}
     * @param messageId           the message id
     * @param authorId            the author id
     * @param author              the author
     * @param messageCreatedAt    the created at timestamp
     * @param messageEditedAt     the edited at timestamp
     * @param messageDeletedAt    the deleted at timestamp
     * @param referencesMessageId the referenced message id
     * @param referencesMessage   the referenced message
     * @param attachments         the message attachements
     */
    @SuppressWarnings("java:S107")
    private TicketMessage(Optional<Long> id, Ticket ticket, Optional<Message> message, String messageId,
            String authorId,
            User author, LocalDateTime messageCreatedAt, Optional<LocalDateTime> messageEditedAt,
            Optional<LocalDateTime> messageDeletedAt, Optional<String> referencesMessageId,
            Optional<Message> referencesMessage, List<TicketMessageAttachement> attachments, boolean botMessage) {
        this.id = id;
        this.ticket = ticket;
        this.message = message;
        this.messageId = messageId;
        this.authorId = authorId;
        this.author = author;
        this.messageCreatedAt = messageCreatedAt;
        this.messageEditedAt = messageEditedAt;
        this.messageDeletedAt = messageDeletedAt;
        this.referencesMessageId = referencesMessageId;
        this.referencesMessage = referencesMessage;
        this.attachments = attachments;
        this.botMessage = botMessage;
    }

    /**
     * Delete a ticket message
     *
     * @param messageId
     *                  the message id
     * @return the {@link SurfFutureResult}
     */
    public SurfFutureResult<Optional<TicketMessage>> delete() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            if (this.messageDeletedAt.isPresent()) {
                return Optional.of(this);
            }

            TicketMessage newMessage = new TicketMessage(this);
            newMessage.messageDeletedAt = Optional.of(LocalDateTime.now());

            return newMessage.create().join();
        });
    }

    /**
     * Returns the parameters of the ticket message
     *
     * @return the parameters of the ticket message
     */
    @SuppressWarnings("java:S1192")
    public Map<String, String> toParameters() {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("message_id", messageId);
        parameters.put("author_id", authorId);
        parameters.put("bot_message", String.valueOf(botMessage ? 1 : 0));

        if (referencesMessageId.isPresent()) {
            parameters.put("references_message_id", referencesMessageId.get());
        }

        if (message.isPresent()) {
            parameters.put("content", message.get().getContentDisplay());
        }

        parameters.put("message_created_at", messageCreatedAt.toString());

        if (messageEditedAt.isPresent()) {
            parameters.put("message_edited_at", messageEditedAt.get().toString());
        }

        if (messageDeletedAt.isPresent()) {
            parameters.put("message_deleted_at", messageDeletedAt.get().toString());
        }

        JsonArray attachmentsArray = new JsonArray();

        for (TicketMessageAttachement attachement : attachments) {
            JsonObject attachementObject = attachement.toJsonObject();
            attachmentsArray.add(attachementObject);
        }

        parameters.put("attachments", attachmentsArray.toString());

        return parameters;
    }

    /**
     * Form a ticket message by a json object
     *
     * @param ticket     the ticket
     * @param jsonObject the json object
     * @return the ticket message
     */
    @SuppressWarnings({ "java:S6541", "java:S3776" })
    public static TicketMessage fromJsonObject(Ticket ticket, JsonObject jsonObject) {
        Optional<String> channelIdOptional = ticket.getChannelId();
        Optional<TextChannel> channelOptional = Optional.empty();

        if (channelIdOptional.isPresent()) {
            String channelId = channelIdOptional.get();
            channelOptional = Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelId + ""));
        }

        Optional<Long> id = Optional.empty();
        if (jsonObject.has("id")) {
            id = Optional.of(jsonObject.get("id").getAsLong());
        }

        String messageId = null;
        Optional<Message> message = Optional.empty();
        if (jsonObject.has("message_id") && channelOptional.isPresent()) {
            TextChannel channel = channelOptional.get();
            messageId = jsonObject.get("message_id").getAsString() + "";

            try {
                message = Optional.ofNullable(channel.retrieveMessageById(messageId).complete());
            } catch (ErrorResponseException exception) {
                // IGNORE
            } catch (Exception exception) {
                Launcher.getLogger().logError(exception);
            }
        }

        String authorId = null;
        User author = null;
        if (jsonObject.has("author_id")) {
            authorId = jsonObject.get("author_id").getAsString() + "";
            author = DiscordBot.getInstance().getJda().getUserById(authorId);
        }

        LocalDateTime messageCreatedAt = null;
        if (jsonObject.has("message_created_at")) {
            messageCreatedAt = LocalDateTime.parse(jsonObject.get("message_created_at").getAsString().split("\\.")[0]);
        }

        Optional<LocalDateTime> messageEditedAt = Optional.empty();
        if (jsonObject.has("message_edited_at") && jsonObject.get("message_edited_at") != null && !(jsonObject
                .get("message_edited_at") instanceof JsonNull)) {
            messageEditedAt = Optional
                    .of(LocalDateTime.parse(jsonObject.get("message_edited_at").getAsString().split("\\.")[0]));
        }

        Optional<LocalDateTime> messageDeletedAt = Optional.empty();
        if (jsonObject.has("message_deleted_at") && jsonObject.get("message_deleted_at") != null && !(jsonObject
                .get("message_deleted_at") instanceof JsonNull)) {
            messageDeletedAt = Optional
                    .of(LocalDateTime.parse(jsonObject.get("message_deleted_at").getAsString().split("\\.")[0]));
        }

        Optional<String> referencesMessageId = Optional.empty();
        Optional<Message> referencesMessage = Optional.empty();
        if (jsonObject.has("references_message_id") && channelOptional.isPresent()
                && jsonObject.get("references_message_id") != null && !(jsonObject
                        .get("references_message_id") instanceof JsonNull)) {
            TextChannel channel = channelOptional.get();
            String referencesMessageIdString = jsonObject.get("references_message_id").getAsString() + "";
            referencesMessageId = Optional.ofNullable(referencesMessageIdString);
            if (referencesMessageId.isPresent()) {
                referencesMessage = Optional
                        .ofNullable(channel.retrieveMessageById(referencesMessageIdString).complete());
            }
        }

        boolean botMessage = false;
        if (jsonObject.has("bot_message")) {
            botMessage = jsonObject.get("bot_message").getAsBoolean();
        }

        List<TicketMessageAttachement> attachments = new ArrayList<>();
        TicketMessage ticketMessage = new TicketMessage(id, ticket, message, messageId, authorId, author,
                messageCreatedAt, messageEditedAt,
                messageDeletedAt, referencesMessageId, referencesMessage, attachments, botMessage);

        if (jsonObject.has("attachments")) {
            JsonElement attachmentsElement = jsonObject.get("attachments");
            if (!attachmentsElement.isJsonArray()) {
                return null;
            }

            JsonArray attachmentsArray = attachmentsElement.getAsJsonArray();

            for (JsonElement attachementElement : attachmentsArray) {
                if (!attachementElement.isJsonObject()) {
                    continue;
                }

                JsonObject attachementObject = attachementElement.getAsJsonObject();
                TicketMessageAttachement attachement = TicketMessageAttachement.fromJsonObject(ticketMessage,
                        attachementObject);
                attachments.add(attachement);
            }
        }

        ticketMessage.attachments = attachments;

        return ticketMessage;
    }

    /**
     * Saves the ticket message
     *
     * @return True if the ticket message has been saved successfully, false
     */
    public SurfFutureResult<Optional<TicketMessage>> create() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            if (ticket.getTicketId().isEmpty()) {
                return Optional.empty();
            }

            String ticketId = ticket.getTicketId().get();

            String url = String.format(API.TICKET_MESSAGES, ticketId);
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
            WebResponse response = request.executePost().join();

            Object responseBody = response.getBody();
            String bodyString = responseBody.toString();

            if (!(response.getStatusCode() == 201 || response.getStatusCode() == 200)) {
                Launcher.getLogger().logWarn("Ticket message could not be created: " + bodyString);
                return Optional.empty();
            }

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                return Optional.empty();
            }

            JsonObject jsonObject = (JsonObject) bodyElement.get("data");

            TicketMessage tempMessage = fromJsonObject(ticket, jsonObject);
            id = tempMessage.id;

            return Optional.of(this);
        });
    }

    /**
     * Updates the ticket message
     *
     * @param updatedMessage The updated message
     * @return The future result
     */
    public SurfFutureResult<Optional<TicketMessage>> update(Message updatedMessage) {
        CompletableFuture<Optional<TicketMessage>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMessage>> result = new DiscordFutureResult<>(future);

        TicketMessage newTicketMessage = new TicketMessage(this);
        newTicketMessage.message = Optional.of(updatedMessage);
        newTicketMessage.messageId = updatedMessage.getId();

        newTicketMessage.author = updatedMessage.getAuthor();
        newTicketMessage.authorId = newTicketMessage.author.getId();

        newTicketMessage.messageCreatedAt = updatedMessage.getTimeCreated().toLocalDateTime();

        OffsetDateTime timeEdited = updatedMessage.getTimeEdited();
        newTicketMessage.messageEditedAt = Optional.ofNullable(
                updatedMessage.isEdited() && timeEdited != null ? timeEdited.toLocalDateTime() : null);

        MessageReference reference = updatedMessage.getMessageReference();
        if (reference != null) {
            reference.resolve().queue(referencedMessage -> newTicketMessage.referencesMessageId = Optional
                    .of(referencedMessage.getId()));

            newTicketMessage.referencesMessage = Optional.ofNullable(reference.getMessage());
        }

        newTicketMessage.attachments = new ArrayList<>();

        for (Attachment attachment : updatedMessage.getAttachments()) {
            String name = attachment.getFileName();
            String url = attachment.getUrl();
            String extension = attachment.getFileExtension();
            int size = attachment.getSize();

            String description = attachment.getDescription();

            TicketMessageAttachement attachement = new TicketMessageAttachement(newTicketMessage, name, url, extension,
                    size, description);
            newTicketMessage.attachments.add(attachement);
        }

        newTicketMessage.botMessage = updatedMessage.getAuthor().isBot();

        newTicketMessage.create().whenComplete(createdTicketMessageOptional -> {
            if (createdTicketMessageOptional.isEmpty()) {
                future.complete(Optional.empty());
                return;
            }

            TicketMessage createdTicketMessage = createdTicketMessageOptional.get();
            future.complete(Optional.of(createdTicketMessage));
        });

        return result;
    }

    /**
     * Returns the message of the ticket message
     *
     * @return The message of the ticket message
     */
    public Optional<Message> getMessage() {
        return message;
    }

    /**
     * Returns the id of the ticket message
     *
     * @return The id of the ticket message
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the id of the author of the ticket message
     *
     * @return The id of the author of the ticket message
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * Returns the author of the ticket message
     *
     * @return The author of the ticket message
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Returns the creation date of the ticket message
     *
     * @return The creation date of the ticket message
     */
    public LocalDateTime getMessageCreatedAt() {
        return messageCreatedAt;
    }

    /**
     * Returns the date of the last edit of the ticket message
     *
     * @return The date of the last edit of the ticket message
     */
    public Optional<LocalDateTime> getMessageEditedAt() {
        return messageEditedAt;
    }

    /**
     * Returns the date of the deletion of the ticket message
     *
     * @return The date of the deletion of the ticket message
     */
    public Optional<LocalDateTime> getMessageDeletedAt() {
        return messageDeletedAt;
    }

    /**
     * Returns the id of the referenced message of the ticket message
     *
     * @return The id of the referenced message of the ticket message
     */
    public Optional<String> getReferencesMessageId() {
        return referencesMessageId;
    }

    /**
     * Returns the referenced message of the ticket message
     *
     * @return the referencesMessage
     */
    public Optional<Message> getReferencesMessage() {
        return referencesMessage;
    }

    /**
     * Returns the attachments of the ticket message
     *
     * @return The attachments of the ticket message
     */
    public List<TicketMessageAttachement> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<TicketMessageAttachement> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    /**
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

}
