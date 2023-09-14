package dev.slne.discord.ticket.message;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.slne.data.api.DataApi;
import dev.slne.data.api.gson.GsonConverter;
import dev.slne.data.api.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TicketMessage {

    @SerializedName("id")
    private long id;

    @SerializedName("ticket_id")
    private long ticketRawId;

    @SerializedName("content")
    private String jsonContent;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("author_name")
    private String authorName;

    @SerializedName("author_avatar_url")
    private String authorAvatarUrl;

    @SerializedName("message_created_at")
    private ZonedDateTime messageCreatedAt;

    @SerializedName("message_edited_at")
    private ZonedDateTime messageEditedAt;

    @SerializedName("message_deleted_at")
    private ZonedDateTime messageDeletedAt;

    @SerializedName("references_message_id")
    private String referencesMessageId;

    @SerializedName("attachments")
    private List<TicketMessageAttachement> attachments;

    @SerializedName("bot_message")
    private boolean botMessage;

    /**
     * Constructor for a ticket message
     *
     * @param clone The ticket message to clone
     */
    public TicketMessage(TicketMessage clone) {
        this.id = clone.id;
        this.ticketRawId = clone.ticketRawId;
        this.messageId = clone.messageId;
        this.jsonContent = clone.jsonContent;

        this.authorId = clone.authorId;
        this.authorName = clone.authorName;
        this.authorAvatarUrl = clone.authorAvatarUrl;

        this.messageCreatedAt = clone.messageCreatedAt;
        this.messageEditedAt = clone.messageEditedAt;
        this.messageDeletedAt = clone.messageDeletedAt;

        this.referencesMessageId = clone.referencesMessageId;

        this.attachments = new ArrayList<>(clone.attachments);
        this.botMessage = clone.botMessage;
    }

    /**
     * Constructor for a ticket message
     *
     * @param message The message to create the ticket message from
     */
    public TicketMessage(Ticket ticket, Message message) {
        this.ticketRawId = ticket.getId();

        this.messageId = message.getId();
        this.jsonContent = message.getContentDisplay();

        this.authorId = message.getAuthor().getId();
        this.authorName = message.getAuthor().getName();
        this.authorAvatarUrl = message.getAuthor().getAvatarUrl();

        LocalDateTime createdUTC = message.getTimeCreated().toLocalDateTime();
        this.messageCreatedAt = Times.convertFromLocalDateTime(createdUTC);

        OffsetDateTime timeEdited = message.getTimeEdited();
        LocalDateTime editedUTC = timeEdited != null ? timeEdited.toLocalDateTime() : null;
        this.messageEditedAt = editedUTC != null ? Times.convertFromLocalDateTime(editedUTC) : null;

        TextChannel channel = ticket.getChannel();
        MessageReference reference = message.getMessageReference();
        if (reference != null && channel != null) {
            reference.resolve().queue(referencedMessage -> this.referencesMessageId = referencedMessage.getId());
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
     * Returns a ticket message from a json object
     *
     * @param jsonObject The json object
     *
     * @return The ticket message
     */
    private static TicketMessage fromJsonObject(JsonObject jsonObject) {
        GsonConverter gson = DiscordBot.getInstance().getGsonConverter();

        return gson.fromJson(jsonObject.toString(), TicketMessage.class);
    }

    /**
     * Returns a ticket message from a message id
     *
     * @return the ticket message
     */
    public static TicketMessage getByMessageId(long id) {
        return DiscordBot.getInstance().getTicketManager().getTickets().stream().map(Ticket::getMessages)
                .filter(Objects::nonNull).flatMap(List::stream).filter(message -> message.getId() == id)
                .findFirst().orElse(null);
    }

    /**
     * Delete a ticket message
     * the message id
     *
     * @return the {@link CompletableFuture}
     */
    public CompletableFuture<TicketMessage> delete() {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            if (this.messageDeletedAt != null) {
                future.complete(this);
                return;
            }

            TicketMessage newMessage = new TicketMessage(this);
            newMessage.messageDeletedAt = ZonedDateTime.now();

            newMessage.create().thenAcceptAsync(future::complete).exceptionally(throwable -> {
                DataApi.getDataInstance().logError(getClass(), "Ticket message could not be deleted", throwable);
                future.complete(null);
                return null;
            });
        });

        return future;
    }

    /**
     * Returns the parameters of the ticket message
     *
     * @return the parameters of the ticket message
     */
    public Map<String, Object> toParameters() {
        Map<String, Object> parameters = new HashMap<>();

        if (messageId != null) {
            parameters.put("message_id", messageId);
        }

        if (authorId != null) {
            parameters.put("author_id", authorId);
        }

        if (authorName != null) {
            parameters.put("author_name", authorName);
        }

        if (authorAvatarUrl != null) {
            parameters.put("author_avatar_url", authorAvatarUrl);
        }

        if (referencesMessageId != null) {
            parameters.put("references_message_id", referencesMessageId);
        }

        String content = getContent().join();
        if (content != null) {
            parameters.put("content", content);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (messageCreatedAt != null) {
            parameters.put("message_created_at", Times.convertToLocalDateTime(messageCreatedAt).format(formatter));
        }

        if (messageEditedAt != null) {
            parameters.put("message_edited_at", Times.convertToLocalDateTime(messageEditedAt).format(formatter));
        }

        if (messageDeletedAt != null) {
            parameters.put("message_deleted_at", Times.convertToLocalDateTime(messageDeletedAt).format(formatter));
        }

        parameters.put("bot_message", botMessage ? "1" : "0");

        JsonArray attachmentsArray = new JsonArray();

        for (TicketMessageAttachement attachement : attachments) {
            JsonObject attachementObject = attachement.toJsonObject();
            attachmentsArray.add(attachementObject);
        }

        parameters.put("attachments", attachmentsArray.toString());

        return parameters;
    }

    /**
     * Prints the ticket message
     */
    public void printMessage() {
        String content = getContent().join();

        if (getTicket().getChannel() == null || content == null) {
            return;
        }

        getTicket().getWebhook().queue(webhook -> {
            if (webhook == null) {
                return;
            }

            String avatarUrl = authorAvatarUrl;

            try (JDAWebhookClient client = JDAWebhookClient.from(webhook)) {
                WebhookMessageBuilder builder = new WebhookMessageBuilder();
                builder.setUsername(authorName);
                builder.setAvatarUrl(avatarUrl);
                builder.setContent(content);

                client.send(builder.build());
            }
        });
    }

    /**
     * Saves the ticket message
     *
     * @return True if the ticket message has been saved successfully, false
     */
    public CompletableFuture<TicketMessage> create() {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();
        Ticket ticket = getTicket();

        if (ticket == null) {
            future.complete(null);
            return future;
        }

        String ticketId = ticket.getTicketId();

        if (ticketId == null) {
            future.complete(null);
            return future;
        }

        CompletableFuture.runAsync(() -> {
            String url = String.format(API.TICKET_MESSAGES, ticketId);
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters()).build();
            request.executePost().thenAccept(response -> {
                TicketMessage tempMessage =
                        fromJsonObject(response.bodyObject(DiscordBot.getInstance().getGsonConverter()));
                id = tempMessage.id;

                future.complete(this);
            }).exceptionally(throwable -> {
                DataApi.getDataInstance().logError(getClass(), "Ticket message could not be created", throwable);
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return future;
    }

    /**
     * Updates the ticket message
     *
     * @param updatedMessage The updated message
     *
     * @return The future result
     */
    public CompletableFuture<TicketMessage> update(Message updatedMessage) {
        CompletableFuture<TicketMessage> future = new CompletableFuture<>();
        TicketMessage newTicketMessage = new TicketMessage(this);

        newTicketMessage.jsonContent = updatedMessage.getContentDisplay();
        newTicketMessage.messageCreatedAt =
                Times.convertFromLocalDateTime(updatedMessage.getTimeCreated().toLocalDateTime());

        OffsetDateTime timeEdited = updatedMessage.getTimeEdited();
        newTicketMessage.messageEditedAt = updatedMessage.isEdited() && timeEdited != null
                ? Times.convertFromLocalDateTime(timeEdited.toLocalDateTime())
                : null;

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

        newTicketMessage.create().thenAcceptAsync(future::complete).exceptionally(throwable -> {
            DataApi.getDataInstance().logError(getClass(), "Ticket message could not be updated", throwable);
            future.completeExceptionally(throwable);
            return null;
        });

        return future;
    }

    /**
     * @return the message
     */
    public RestAction<Message> getMessage() {
        TextChannel channel = getTicket().getChannel();
        if (channel == null) {
            return null;
        }

        return channel.retrieveMessageById(messageId);
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
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
     * @param authorId the authorId to set
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * @return the author
     */
    public RestAction<User> getAuthor() {
        if (authorId == null) {
            return null;
        }

        return DiscordBot.getInstance().getJda().retrieveUserById(authorId);
    }

    /**
     * Returns the creation date of the ticket message
     *
     * @return The creation date of the ticket message
     */
    public ZonedDateTime getMessageCreatedAt() {
        return messageCreatedAt;
    }

    /**
     * @param messageCreatedAt the messageCreatedAt to set
     */
    public void setMessageCreatedAt(ZonedDateTime messageCreatedAt) {
        this.messageCreatedAt = messageCreatedAt;
    }

    /**
     * Returns the date of the last edit of the ticket message
     *
     * @return The date of the last edit of the ticket message
     */
    public ZonedDateTime getMessageEditedAt() {
        return messageEditedAt;
    }

    /**
     * @param messageEditedAt the messageEditedAt to set
     */
    public void setMessageEditedAt(ZonedDateTime messageEditedAt) {
        this.messageEditedAt = messageEditedAt;
    }

    /**
     * Returns the date of the deletion of the ticket message
     *
     * @return The date of the deletion of the ticket message
     */
    public ZonedDateTime getMessageDeletedAt() {
        return messageDeletedAt;
    }

    /**
     * @param messageDeletedAt the messageDeletedAt to set
     */
    public void setMessageDeletedAt(ZonedDateTime messageDeletedAt) {
        this.messageDeletedAt = messageDeletedAt;
    }

    /**
     * Returns the id of the referenced message of the ticket message
     *
     * @return The id of the referenced message of the ticket message
     */
    public String getReferencesMessageId() {
        return referencesMessageId;
    }

    /**
     * @param referencesMessageId the referencesMessageId to set
     */
    public void setReferencesMessageId(String referencesMessageId) {
        this.referencesMessageId = referencesMessageId;
    }

    /**
     * @return the referencesMessage
     */
    public RestAction<Message> getReferencesMessage() {
        if (referencesMessageId == null) {
            return null;
        }

        TextChannel channel = getTicket().getChannel();
        if (channel == null) {
            return null;
        }

        return channel.retrieveMessageById(referencesMessageId);
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
        if (ticketRawId == 0) {
            return null;
        }

        return DiscordBot.getInstance().getTicketManager().getTicketById(ticketRawId);
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the authorAvatarUrl
     */
    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    /**
     * @param authorAvatarUrl the authorAvatarUrl to set
     */
    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @return the content
     */
    public CompletableFuture<String> getContent() {
        CompletableFuture<String> future = new CompletableFuture<>();

        if (jsonContent != null) {
            future.complete(jsonContent);
            return future;
        }

        RestAction<Message> message = getMessage();

        if (message == null) {
            future.complete(null);
            return future;
        }

        message.queue(msg -> {
            String content = msg.getContentDisplay();
            future.complete(content);
        });

        return future;
    }

    /**
     * @return the jsonContent
     */
    public String getJsonContent() {
        return jsonContent;
    }

    /**
     * @param jsonContent the jsonContent to set
     */
    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    /**
     * @return the ticketId
     */
    public long getTicketRawId() {
        return ticketRawId;
    }

    /**
     * @param ticketId the ticketId to set
     */
    public void setTicketRawId(long ticketId) {
        this.ticketRawId = ticketId;
    }

    /**
     * @param botMessage the botMessage to set
     */
    public void setBotMessage(boolean botMessage) {
        this.botMessage = botMessage;
    }

}
