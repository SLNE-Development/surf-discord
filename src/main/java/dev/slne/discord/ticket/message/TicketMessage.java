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

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.data.core.web.WebResponse;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.Times;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.ticket.Ticket;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class TicketMessage {

    private Optional<Long> id;
    private Ticket ticket;
    private Optional<RestAction<Message>> message;
    private Optional<String> jsonContent;
    private String messageId;

    private String authorId;
    private String authorName;
    private String authorAvatarUrl;
    private RestAction<User> author;

    private LocalDateTime messageCreatedAt;
    private Optional<LocalDateTime> messageEditedAt;
    private Optional<LocalDateTime> messageDeletedAt;

    private Optional<String> referencesMessageId;
    private Optional<RestAction<Message>> referencesMessage;

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
        this.jsonContent = clone.jsonContent;

        this.authorId = clone.authorId;
        this.authorName = clone.authorName;
        this.authorAvatarUrl = clone.authorAvatarUrl;
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

        Optional<TextChannel> channelOptional = ticket.getChannel();
        if (channelOptional.isPresent()) {
            TextChannel channel = channelOptional.get();
            this.message = Optional.of(channel.retrieveMessageById(message.getId()));
        }
        this.messageId = message.getId();
        this.jsonContent = Optional.ofNullable(message.getContentDisplay());

        this.author = DiscordBot.getInstance().getJda().retrieveUserById(message.getAuthor().getId());
        this.authorId = message.getAuthor().getId();
        this.authorName = message.getAuthor().getName();
        this.authorAvatarUrl = message.getAuthor().getAvatarUrl();

        this.messageCreatedAt = message.getTimeCreated().toLocalDateTime();

        OffsetDateTime timeEdited = message.getTimeEdited();
        this.messageEditedAt = Optional.ofNullable(
                message.isEdited() && timeEdited != null ? timeEdited.toLocalDateTime() : null);

        this.messageDeletedAt = Optional.empty();

        MessageReference reference = message.getMessageReference();
        this.referencesMessage = Optional.empty();
        this.referencesMessageId = Optional.empty();
        if (reference != null && channelOptional.isPresent()) {
            TextChannel channel = channelOptional.get();
            reference.resolve().queue(referencedMessage -> this.referencesMessageId = Optional.of(
                    referencedMessage.getId()));

            this.referencesMessage = Optional.ofNullable(channel.retrieveMessageById(reference.getMessageId()));
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
     * @param botMessage          if the message is a bot message
     * @param jsonContent         the json content
     */
    @SuppressWarnings("java:S107")
    private TicketMessage(Optional<Long> id, Ticket ticket,
            Optional<RestAction<Message>> message,
            String messageId,
            String authorId, String authorName, String authorAvatarUrl,
            RestAction<User> author, LocalDateTime messageCreatedAt, Optional<LocalDateTime> messageEditedAt,
            Optional<LocalDateTime> messageDeletedAt, Optional<String> referencesMessageId,
            Optional<RestAction<Message>> referencesMessage, List<TicketMessageAttachement> attachments,
            boolean botMessage, Optional<String> jsonContent) {
        this.id = id;
        this.ticket = ticket;
        this.message = message;
        this.messageId = messageId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.author = author;
        this.messageCreatedAt = messageCreatedAt;
        this.messageEditedAt = messageEditedAt;
        this.messageDeletedAt = messageDeletedAt;
        this.referencesMessageId = referencesMessageId;
        this.referencesMessage = referencesMessage;
        this.attachments = attachments;
        this.botMessage = botMessage;
        this.jsonContent = jsonContent;
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
            newMessage.messageDeletedAt = Optional.of(Times.now());

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

        if (referencesMessageId.isPresent()) {
            parameters.put("references_message_id", referencesMessageId.get());
        }

        Optional<String> contentOptional = getContent().join();
        if (contentOptional.isPresent()) {
            parameters.put("content", contentOptional.get());
        }

        if (messageCreatedAt != null) {
            parameters.put("message_created_at", messageCreatedAt.toString());
        }

        if (messageEditedAt.isPresent()) {
            parameters.put("message_edited_at", messageEditedAt.get().toString());
        }

        if (messageDeletedAt.isPresent()) {
            parameters.put("message_deleted_at", messageDeletedAt.get().toString());
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
        Optional<Long> id = Optional.empty();
        String messageId = null;
        Optional<RestAction<Message>> message = Optional.empty();
        String authorId = null;
        String authorName = null;
        String authorAvatarUrl = null;
        RestAction<User> author = null;
        LocalDateTime messageCreatedAt = null;
        Optional<LocalDateTime> messageEditedAt = Optional.empty();
        Optional<LocalDateTime> messageDeletedAt = Optional.empty();
        Optional<String> referencesMessageId = Optional.empty();
        Optional<RestAction<Message>> referencesMessage = Optional.empty();
        boolean botMessage = false;
        List<TicketMessageAttachement> attachments = new ArrayList<>();
        Optional<String> jsonContent = Optional.empty();

        TicketMessage ticketMessage = new TicketMessage(id, ticket, message, messageId, authorId, authorName,
                authorAvatarUrl, author,
                messageCreatedAt, messageEditedAt,
                messageDeletedAt, referencesMessageId, referencesMessage, attachments, botMessage, jsonContent);

        if (channelIdOptional.isPresent()) {
            String channelId = channelIdOptional.get();

            if (channelId != null) {
                channelOptional = Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelId));
            }
        }

        if (jsonObject.has("id")) {
            id = Optional.of(jsonObject.get("id").getAsLong());
            ticketMessage.setId(id);
        }

        if (jsonObject.has("message_id") && channelOptional.isPresent()) {
            TextChannel channel = channelOptional.get();
            messageId = jsonObject.get("message_id").getAsString();

            if (messageId != null) {
                message = Optional.of(channel.retrieveMessageById(messageId));
                ticketMessage.setMessage(message);
            }

            ticketMessage.setMessageId(messageId);
        }

        if (jsonObject.has("author_id")) {
            authorId = jsonObject.get("author_id").getAsString();
            if (authorId != null) {
                author = DiscordBot.getInstance().getJda().retrieveUserById(authorId);
                ticketMessage.setAuthor(author);
            }

            ticketMessage.setAuthorId(authorId);
        }

        if (jsonObject.has("author_name") && jsonObject.get("author_name") != null && !(jsonObject
                .get("author_name") instanceof JsonNull)) {
            authorName = jsonObject.get("author_name").getAsString();
            ticketMessage.setAuthorName(authorName);
        }

        if (jsonObject.has("author_avatar_url") && jsonObject.get("author_avatar_url") != null && !(jsonObject
                .get("author_avatar_url") instanceof JsonNull)) {
            authorAvatarUrl = jsonObject.get("author_avatar_url").getAsString();
            ticketMessage.setAuthorAvatarUrl(authorAvatarUrl);
        }

        if (jsonObject.has("content") && jsonObject.get("content") != null && !(jsonObject
                .get("content") instanceof JsonNull)) {
            String content = jsonObject.get("content").getAsString();
            ticketMessage.setJsonContent(Optional.of(content));
        }

        if (jsonObject.has("message_created_at")) {
            messageCreatedAt = LocalDateTime.parse(jsonObject.get("message_created_at").getAsString().split("\\.")[0]);
            ticketMessage.setMessageCreatedAt(messageCreatedAt);
        }

        if (jsonObject.has("message_edited_at") && jsonObject.get("message_edited_at") != null && !(jsonObject
                .get("message_edited_at") instanceof JsonNull)) {
            messageEditedAt = Optional
                    .of(LocalDateTime.parse(jsonObject.get("message_edited_at").getAsString().split("\\.")[0]));
            ticketMessage.setMessageEditedAt(messageEditedAt);
        }

        if (jsonObject.has("message_deleted_at") && jsonObject.get("message_deleted_at") != null && !(jsonObject
                .get("message_deleted_at") instanceof JsonNull)) {
            messageDeletedAt = Optional
                    .of(LocalDateTime.parse(jsonObject.get("message_deleted_at").getAsString().split("\\.")[0]));
            ticketMessage.setMessageDeletedAt(messageDeletedAt);
        }

        if (jsonObject.has("references_message_id") && channelOptional.isPresent()
                && jsonObject.get("references_message_id") != null && !(jsonObject
                        .get("references_message_id") instanceof JsonNull)) {
            TextChannel channel = channelOptional.get();
            String referencesMessageIdString = jsonObject.get("references_message_id").getAsString();
            referencesMessageId = Optional.ofNullable(referencesMessageIdString);
            if (referencesMessageId.isPresent() && referencesMessageIdString != null) {
                referencesMessage = Optional
                        .ofNullable(channel.retrieveMessageById(referencesMessageIdString));
                ticketMessage.setReferencesMessage(referencesMessage);
            }

            ticketMessage.setReferencesMessageId(referencesMessageId);
        }

        if (jsonObject.has("bot_message")) {
            botMessage = jsonObject.get("bot_message").getAsBoolean();
            ticketMessage.setBotMessage(botMessage);
        }

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

            ticketMessage.setAttachments(attachments);
        }

        return ticketMessage;
    }

    /**
     * Prints the ticket message
     */
    public void printMessage() {
        Optional<String> content = getContent().join();

        if (getTicket().getChannel().isEmpty() || content.isEmpty()) {
            return;
        }

        Optional<Webhook> webhookOptional = getTicket().getWebhook();
        if (webhookOptional.isEmpty()) {
            return;
        }

        Webhook webhook = webhookOptional.get();

        String avatarUrl = authorAvatarUrl;

        try (JDAWebhookClient client = JDAWebhookClient.from(webhook)) {
            WebhookMessageBuilder builder = new WebhookMessageBuilder();
            builder.setUsername(authorName);
            builder.setAvatarUrl(avatarUrl);
            builder.setContent(content.get());

            client.send(builder.build()).join();
        }
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
                Launcher.getLogger().logError("Ticket message could not be created: " + bodyString);
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

        Optional<TextChannel> channelOptional = ticket.getChannel();
        if (channelOptional.isPresent()) {
            TextChannel channel = channelOptional.get();
            newTicketMessage.message = Optional.of(channel.retrieveMessageById(updatedMessage.getId()));
        }

        newTicketMessage.jsonContent = Optional.ofNullable(updatedMessage.getContentDisplay());
        newTicketMessage.messageCreatedAt = updatedMessage.getTimeCreated().toLocalDateTime();

        OffsetDateTime timeEdited = updatedMessage.getTimeEdited();
        newTicketMessage.messageEditedAt = Optional.ofNullable(
                updatedMessage.isEdited() && timeEdited != null ? timeEdited.toLocalDateTime() : null);

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
     * @return the message
     */
    public Optional<RestAction<Message>> getMessage() {
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
     * @return the author
     */
    public RestAction<User> getAuthor() {
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
     * @return the referencesMessage
     */
    public Optional<RestAction<Message>> getReferencesMessage() {
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

    /**
     * @return the authorAvatarUrl
     */
    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    /**
     * @return the authorName
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * @return the content
     */
    public SurfFutureResult<Optional<String>> getContent() {
        CompletableFuture<Optional<String>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<String>> result = new DiscordFutureResult<>(future);

        if (jsonContent.isPresent()) {
            future.complete(jsonContent);
            return result;
        }

        if (message.isEmpty()) {
            future.complete(Optional.empty());
            return result;
        }

        message.get().queue(msg -> {
            String content = msg.getContentDisplay();
            future.complete(Optional.of(content));
        });

        return result;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(RestAction<User> author) {
        this.author = author;
    }

    /**
     * @param authorAvatarUrl the authorAvatarUrl to set
     */
    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    /**
     * @param authorId the authorId to set
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * @param authorName the authorName to set
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * @param botMessage the botMessage to set
     */
    public void setBotMessage(boolean botMessage) {
        this.botMessage = botMessage;
    }

    /**
     * @param jsonContent the jsonContent to set
     */
    public void setJsonContent(Optional<String> jsonContent) {
        this.jsonContent = jsonContent;
    }

    /**
     * @param id the id to set
     */
    public void setId(Optional<Long> id) {
        this.id = id;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Optional<RestAction<Message>> message) {
        this.message = message;
    }

    /**
     * @param messageCreatedAt the messageCreatedAt to set
     */
    public void setMessageCreatedAt(LocalDateTime messageCreatedAt) {
        this.messageCreatedAt = messageCreatedAt;
    }

    /**
     * @param messageDeletedAt the messageDeletedAt to set
     */
    public void setMessageDeletedAt(Optional<LocalDateTime> messageDeletedAt) {
        this.messageDeletedAt = messageDeletedAt;
    }

    /**
     * @param messageEditedAt the messageEditedAt to set
     */
    public void setMessageEditedAt(Optional<LocalDateTime> messageEditedAt) {
        this.messageEditedAt = messageEditedAt;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @param referencesMessage the referencesMessage to set
     */
    public void setReferencesMessage(Optional<RestAction<Message>> referencesMessage) {
        this.referencesMessage = referencesMessage;
    }

    /**
     * @param referencesMessageId the referencesMessageId to set
     */
    public void setReferencesMessageId(Optional<String> referencesMessageId) {
        this.referencesMessageId = referencesMessageId;
    }

    /**
     * @param ticket the ticket to set
     */
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

}
