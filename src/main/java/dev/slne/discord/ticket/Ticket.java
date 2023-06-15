package dev.slne.discord.ticket;

import java.time.LocalDateTime;
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
import dev.slne.discord.discord.guild.DiscordGuild;
import dev.slne.discord.discord.guild.DiscordGuilds;
import dev.slne.discord.ticket.message.TicketMessage;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Ticket {

    private Optional<Long> id;
    private Optional<String> ticketId;
    private LocalDateTime openedAt;

    private Optional<String> guildId;
    private Optional<Guild> guild;

    private Optional<String> channelId;
    private Optional<TextChannel> channel;

    private String ticketTypeString;
    private TicketType ticketType;

    private String ticketAuthorId;
    private User ticketAuthor;

    private Optional<String> closedById;
    private Optional<User> closedBy;

    private Optional<String> closedReason;
    private Optional<LocalDateTime> closedAt;

    private List<TicketMessage> messages;

    /**
     * Constructor for a ticket
     *
     * @param guild        The guild the ticket is created in
     * @param ticketAuthor The author of the ticket
     * @param ticketType   The type of the ticket
     */
    public Ticket(Guild guild, User ticketAuthor, TicketType ticketType) {
        this.id = Optional.empty();
        this.ticketId = Optional.empty();
        this.openedAt = LocalDateTime.now();

        this.guildId = Optional.of(guild.getId());
        this.guild = Optional.of(guild);

        this.channelId = Optional.empty();
        this.channel = Optional.empty();

        this.ticketTypeString = ticketType.name();
        this.ticketType = ticketType;

        this.ticketAuthorId = ticketAuthor.getId();
        this.ticketAuthor = ticketAuthor;

        this.closedById = Optional.empty();
        this.closedBy = Optional.empty();

        this.closedReason = Optional.empty();
        this.closedAt = Optional.empty();

        this.messages = new ArrayList<>();
    }

    /**
     * Constructor for a ticket
     *
     * @param id               The id of the ticket
     * @param ticketId         The ticket id
     * @param openedAt         The date the ticket was opened
     * @param guildId          The id of the guild the ticket is created in
     * @param guild            The guild the ticket is created in
     * @param channelId        The id of the channel the ticket is created in
     * @param channel          The channel the ticket is created in
     * @param ticketTypeString The type of the ticket as a string
     * @param ticketType       The type of the ticket
     * @param ticketAuthorId   The id of the author of the ticket
     * @param ticketAuthor     The author of the ticket
     * @param closedById       The id of the user that closed the ticket
     * @param closedBy         The user that closed the ticket
     * @param closedReason     The reason the ticket was closed
     * @param closedAt         The date the ticket was closed
     */
    @SuppressWarnings("java:S107")
    public Ticket(
            Optional<Long> id, Optional<String> ticketId, LocalDateTime openedAt, Optional<String> guildId,
            Optional<Guild> guild, Optional<String> channelId, Optional<TextChannel> channel, String ticketTypeString,
            TicketType ticketType, String ticketAuthorId, User ticketAuthor, Optional<String> closedById,
            Optional<User> closedBy, Optional<String> closedReason, Optional<LocalDateTime> closedAt,
            List<TicketMessage> messages) {
        this.id = id;
        this.ticketId = ticketId;
        this.openedAt = openedAt;
        this.guildId = guildId;
        this.guild = guild;
        this.channelId = channelId;
        this.channel = channel;
        this.ticketTypeString = ticketTypeString;
        this.ticketType = ticketType;
        this.ticketAuthorId = ticketAuthorId;
        this.ticketAuthor = ticketAuthor;
        this.closedById = closedById;
        this.closedBy = closedBy;
        this.closedReason = closedReason;
        this.closedAt = closedAt;
        this.messages = messages;
    }

    public void afterOpen() {
        // Implemented by subclasses
    }

    /**
     * Get the ticket by the id
     *
     * @param <T>     The type of the ticket
     * @param channel The channel the ticket is created in
     * @return The ticket
     */
    @SuppressWarnings({ "java:S1192", "java:S1141" })
    public static Optional<Ticket> getTicketByChannel(String channelId) {
        return DiscordBot.getInstance().getTicketManager().getTicket(channelId);
    }

    /**
     * Adds a ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     * @return The result of the ticket message adding
     */
    public SurfFutureResult<Optional<TicketMessage>> addTicketMessage(TicketMessage ticketMessage) {
        CompletableFuture<Optional<TicketMessage>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<TicketMessage>> futureResult = new DiscordFutureResult<>(future);

        ticketMessage.create().whenComplete(ticketMessageCallback -> {
            if (ticketMessageCallback.isEmpty()) {
                future.complete(Optional.empty());
                return;
            }

            addRawTicketMessage(ticketMessage);
            future.complete(ticketMessageCallback);
        });

        return futureResult;
    }

    /**
     * Adds a raw ticket message to the ticket
     *
     * @param ticketMessage The ticket message
     */
    public void addRawTicketMessage(TicketMessage ticketMessage) {
        messages.add(ticketMessage);
    }

    /**
     * Get the active tickets
     *
     * @return The active tickets
     */
    public static SurfFutureResult<Optional<List<Ticket>>> getActiveTickets() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.ACTIVE_TICKETS).build();
            WebResponse response = request.executeGet().join();
            List<Ticket> tickets = new ArrayList<>();

            if (response.getStatusCode() != 200) {
                return Optional.of(tickets);
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                return Optional.of(tickets);
            }

            JsonArray dataArray = (JsonArray) bodyElement.get("data");

            for (JsonElement ticketElement : dataArray) {
                if (!ticketElement.isJsonObject()) {
                    continue;
                }

                JsonObject ticketObject = ticketElement.getAsJsonObject();
                Ticket ticket = ticketByJson(ticketObject);

                tickets.add(ticket);
            }
            return Optional.of(tickets);
        });
    }

    /**
     * Returns the ticket by the jsonobject
     *
     * @param jsonObject The json object
     * @return The ticket
     */
    @SuppressWarnings("java:S3776")
    private static Ticket ticketByJson(JsonObject jsonObject) {
        Optional<Long> id = Optional.empty();
        if (jsonObject.has("id")) {
            id = Optional.of(jsonObject.get("id").getAsLong());
        }

        Optional<String> ticketId = Optional.empty();
        if (jsonObject.has("ticket_id")) {
            ticketId = Optional.of(jsonObject.get("ticket_id").getAsString());
        }

        LocalDateTime openedAt = null;
        if (jsonObject.has("opened_at")) {
            openedAt = LocalDateTime.parse(jsonObject.get("opened_at").getAsString().split("\\.")[0]);
        }

        Optional<String> guildId = Optional.empty();
        Optional<Guild> guild = Optional.empty();
        if (jsonObject.has("guild_id") && jsonObject.get("guild_id") != null && !(jsonObject
                .get("guild_id") instanceof JsonNull)) {
            String guildIdString = jsonObject.get("guild_id").getAsString() + "";
            guildId = Optional.of(guildIdString);
            guild = Optional.ofNullable(DiscordBot.getInstance().getJda().getGuildById(guildIdString));
        }

        Optional<String> channelId = Optional.empty();
        Optional<TextChannel> channel = Optional.empty();
        if (jsonObject.has("channel_id") && jsonObject.get("channel_id") != null && !(jsonObject
                .get("channel_id") instanceof JsonNull)) {
            String channelIdString = jsonObject.get("channel_id").getAsString() + "";
            channelId = Optional.of(channelIdString);
            channel = Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelIdString));
        }

        String ticketTypeString = null;
        TicketType ticketType = null;
        if (jsonObject.has("type")) {
            ticketTypeString = jsonObject.get("type").getAsString();
            ticketType = TicketType.getByName(ticketTypeString);
        }

        String ticketAuthorId = null;
        User ticketAuthor = null;
        if (jsonObject.has("author_id") && jsonObject.get("author_id") != null && !(jsonObject
                .get("author_id") instanceof JsonNull)) {
            ticketAuthorId = jsonObject.get("author_id").getAsString() + "";
            ticketAuthor = DiscordBot.getInstance().getJda().getUserById(ticketAuthorId);
        }

        Optional<String> closedById = Optional.empty();
        Optional<User> closedBy = Optional.empty();
        if (jsonObject.has("closed_by_id") && jsonObject.get("closed_by_id") != null && !(jsonObject
                .get("closed_by_id") instanceof JsonNull)) {
            String closedByIdString = jsonObject.get("closed_by_id").getAsString() + "";
            closedById = Optional.of(closedByIdString);
            closedBy = Optional.ofNullable(DiscordBot.getInstance().getJda().getUserById(closedByIdString));
        }

        Optional<String> closedReason = Optional.empty();
        if (jsonObject.has("closed_reason") && jsonObject.get("closed_reason") != null && !(jsonObject
                .get("closed_reason") instanceof JsonNull)) {
            closedReason = Optional.of(jsonObject.get("closed_reason").getAsString());
        }

        Optional<LocalDateTime> closedAt = Optional.empty();
        if (jsonObject.has("closed_at") && jsonObject.get("closed_at") != null && !(jsonObject
                .get("closed_at") instanceof JsonNull)) {
            closedAt = Optional.of(LocalDateTime.parse(jsonObject.get("closed_at").getAsString().split("\\.")[0]));
        }

        List<TicketMessage> messages = new ArrayList<>();

        Ticket ticket = new Ticket(id, ticketId, openedAt, guildId, guild, channelId, channel, ticketTypeString,
                ticketType, ticketAuthorId, ticketAuthor, closedById, closedBy, closedReason, closedAt, messages);

        if (jsonObject.has("messages")) {
            JsonArray messagesArray = jsonObject.get("messages").getAsJsonArray();

            for (JsonElement messageElement : messagesArray) {
                if (!messageElement.isJsonObject()) {
                    continue;
                }

                JsonObject messageObject = messageElement.getAsJsonObject();
                TicketMessage message = TicketMessage.fromJsonObject(ticket, messageObject);

                messages.add(message);
            }
        }

        ticket.messages = messages;

        return ticket;
    }

    /**
     * Returns a map of the parameters
     *
     * @return The map of the parameters
     */
    private Map<String, String> toParameters() {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("author_id", ticketAuthorId);
        parameters.put("type", ticketTypeString);
        parameters.put("channel_id", channelId.orElse(""));
        parameters.put("guild_id", guildId.orElse(""));

        parameters.put("closed_by_id", closedById.orElse(""));
        parameters.put("closed_reason", closedReason.orElse(""));

        return parameters;
    }

    /**
     * Save the ticket
     *
     * @return The result of the ticket saving
     */
    public SurfFutureResult<Optional<Ticket>> createTicket() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.TICKETS).parameters(toParameters()).build();
            WebResponse response = request.executePost().join();

            if (response.getStatusCode() != 201) {
                Launcher.getLogger().logError(response.getBody());
                return Optional.empty();
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                return Optional.empty();
            }

            JsonObject jsonObject = (JsonObject) bodyElement.get("data");
            Ticket newTicket = ticketByJson(jsonObject);

            openedAt = newTicket.openedAt;
            ticketId = newTicket.ticketId;
            id = newTicket.id;

            DiscordBot.getInstance().getTicketManager().addTicket(newTicket);

            return Optional.of(this);
        });
    }

    /**
     * Create the ticket channel
     *
     * @param guild The guild the ticket should be created in
     * @return The result of the ticket creation
     */
    public SurfFutureResult<Optional<TicketCreateResult>> createTicketChannel(Guild guild) {
        return DataApi.getDataInstance().supplyAsync(() -> {
            DiscordGuild discordGuild = DiscordGuilds.getGuild(guild);

            if (discordGuild == null) {
                return Optional.of(TicketCreateResult.GUILD_NOT_FOUND);
            }

            String categoryId = discordGuild.getCategoryId();
            if (categoryId == null) {
                return Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND);
            }

            Category channelCategory = guild.getCategoryById(categoryId);

            if (channelCategory == null) {
                return Optional.of(TicketCreateResult.CATEGORY_NOT_FOUND);
            }

            String ticketName = ticketType.name().toLowerCase() + "-" + ticketAuthor.getName().toLowerCase();
            boolean ticketExists = channelCategory.getChannels().stream()
                    .anyMatch(categoryChannel -> categoryChannel.getName().equalsIgnoreCase(ticketName));

            if (ticketExists) {
                return Optional.of(TicketCreateResult.ALREADY_EXISTS);
            }

            TextChannel ticketChannel = channelCategory.createTextChannel(ticketName).complete();

            this.channelId = Optional.of(ticketChannel.getId());
            this.channel = Optional.of(ticketChannel);

            Optional<Ticket> newTicket = createTicket().join();

            if (newTicket.isEmpty()) {
                return Optional.of(TicketCreateResult.ERROR);
            }

            return Optional.of(TicketCreateResult.SUCCESS);
        });
    }

    public SurfFutureResult<Optional<Ticket>> closeTicket() {
        return DataApi.getDataInstance().supplyAsync(() -> {
            String url = String.format(API.TICKETS, getTicketId().orElse(null));

            WebRequest request = WebRequest.builder().url(url).parameters(toParameters()).build();
            WebResponse response = request.executeDelete().join();

            if (response.getStatusCode() != 200) {
                return Optional.empty();
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                return Optional.empty();
            }

            JsonObject jsonObject = (JsonObject) bodyElement.get("data");
            Ticket newTicket = ticketByJson(jsonObject);

            closedAt = newTicket.closedAt;

            return Optional.of(this);
        });
    }

    /**
     * Close the ticket channel
     *
     * @param user   The user that closed the ticket
     * @param reason The reason the ticket was closed
     * @return The result of the ticket closing
     */
    public SurfFutureResult<TicketCloseResult> closeTicketChannel(User user, String reason) {
        CompletableFuture<TicketCloseResult> future = new CompletableFuture<>();
        DiscordFutureResult<TicketCloseResult> futureResult = new DiscordFutureResult<>(future);

        CompletableFuture.runAsync(() -> {
            if (channel.isEmpty()) {
                future.complete(TicketCloseResult.TICKET_NOT_FOUND);
                return;
            }

            this.closedById = Optional.of(user.getId());
            this.closedBy = Optional.of(user);

            this.closedReason = Optional.of(reason);

            TextChannel textChannel = channel.get();

            textChannel.delete().queue(success -> closeTicket().whenComplete(newTicketOptional -> {
                if (newTicketOptional.isEmpty()) {
                    future.complete(TicketCloseResult.ERROR);
                    return;
                }

                future.complete(TicketCloseResult.SUCCESS);
            }), error -> {
                future.complete(TicketCloseResult.ERROR);

                Launcher.getLogger().logError("Error while closing ticket: " + error.getMessage());
                error.printStackTrace();
            });
        });

        return futureResult;
    }

    /**
     * Returns the ticket message by the message
     *
     * @param message The message
     * @return The ticket message
     */
    public Optional<TicketMessage> getTicketMessage(Message message) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(message.getId()))
                .findFirst();
    }

    /**
     * Returns the ticket message by the message id
     *
     * @param messageId The message id
     * @return The ticket message
     */
    public Optional<TicketMessage> getTicketMessage(String messageId) {
        return messages.stream().filter(ticketMessage -> ticketMessage.getMessageId().equals(messageId)).findFirst();
    }

    /**
     * Get the ticket id
     *
     * @return The ticket id
     */
    public Optional<String> getTicketId() {
        return ticketId;
    }

    /**
     * Get the author of the ticket
     *
     * @return The author of the ticket
     */
    public User getTicketAuthor() {
        return ticketAuthor;
    }

    /**
     * Get the id of the author of the ticket
     *
     * @return The id of the author of the ticket
     */
    public String getTicketAuthorId() {
        return ticketAuthorId;
    }

    /**
     * Get the type of the ticket
     *
     * @return The type of the ticket
     */
    public TicketType getTicketType() {
        return ticketType;
    }

    /**
     * Get the type of the ticket as a string
     *
     * @return The type of the ticket as a string
     */
    public String getTicketTypeString() {
        return ticketTypeString;
    }

    /**
     * Get the opened at date of the ticket
     *
     * @return The opened at date of the ticket
     */
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    /**
     * Get the id of the guild the ticket is created in
     *
     * @return The id of the guild the ticket is created in
     */
    public Optional<String> getGuildId() {
        return guildId;
    }

    /**
     * Get the guild the ticket is created in
     *
     * @return The guild the ticket is created in
     */
    public Optional<Guild> getGuild() {
        return guild;
    }

    /**
     * Get the id of the channel the ticket is created in
     *
     * @return The id of the channel the ticket is created in
     */
    public Optional<String> getChannelId() {
        return channelId;
    }

    /**
     * Get the channel the ticket is created in
     *
     * @return The channel the ticket is created in
     */
    public Optional<TextChannel> getChannel() {
        return channel;
    }

    /**
     * Get the id of the user that closed the ticket
     *
     * @return The id of the user that closed the ticket
     */
    public Optional<String> getClosedById() {
        return closedById;
    }

    /**
     * Get the user that closed the ticket
     *
     * @return The user that closed the ticket
     */
    public Optional<User> getClosedBy() {
        return closedBy;
    }

    /**
     * Get the reason the ticket was closed
     *
     * @return The reason the ticket was closed
     */
    public Optional<String> getClosedReason() {
        return closedReason;
    }

    /**
     * Get the date the ticket was closed
     *
     * @return The date the ticket was closed
     */
    public Optional<LocalDateTime> getClosedAt() {
        return closedAt;
    }

    /**
     * Returns the id of the ticket
     *
     * @return the id
     */
    public Optional<Long> getId() {
        return id;
    }

    /**
     * @return the messages
     */
    public List<TicketMessage> getMessages() {
        return messages;
    }

}
