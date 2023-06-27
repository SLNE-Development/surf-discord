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
import dev.slne.discord.datasource.WebhookHelper;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TicketRepository {

    /**
     * The ticket repository instance
     */
    private TicketRepository() {
    }

    /**
     * Get the active tickets
     *
     * @return The active tickets
     */
    public static SurfFutureResult<Optional<List<Ticket>>> getActiveTickets() {
        CompletableFuture<Optional<List<Ticket>>> future = new CompletableFuture<>();
        DiscordFutureResult<Optional<List<Ticket>>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.ACTIVE_TICKETS).build();
            WebResponse response = request.executeGet().join();
            List<Ticket> tickets = new ArrayList<>();

            if (response.getStatusCode() != 200) {
                Launcher.getLogger().logError(response.getBody());
                future.complete(Optional.of(tickets));
                return;
            }

            Object body = response.getBody();
            String bodyString = body.toString();

            GsonConverter gson = new GsonConverter();
            JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

            if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                future.complete(Optional.of(tickets));
                return;
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

            future.complete(Optional.of(tickets));
        });

        return futureResult;
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
     * Save a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket saving
     */
    public static SurfFutureResult<Optional<Ticket>> createTicket(Ticket ticket) {
        return DataApi.getDataInstance().supplyAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.TICKETS).json(true).parameters(toParameters(ticket))
                    .build();
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

            ticket.setOpenedAt(newTicket.getOpenedAt());
            ticket.setTicketId(newTicket.getTicketId());
            ticket.setId(newTicket.getId());

            DiscordBot.getInstance().getTicketManager().addTicket(ticket);

            return Optional.of(ticket);
        });
    }

    /**
     * Update a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket updating
     */
    public static SurfFutureResult<Optional<Ticket>> updateTicket(Ticket ticket) {
        if (ticket.getTicketId().isEmpty()) {
            return DataApi.getDataInstance().supplyAsync(Optional::empty);
        }

        return DataApi.getDataInstance().supplyAsync(() -> {
            String url = String.format(API.TICKET, ticket.getTicketId().get());
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket))
                    .build();
            WebResponse response = request.executePut().join();

            if (!(response.getStatusCode() == 200 || response.getStatusCode() == 201)) {
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

            return Optional.of(ticket);
        });
    }

    /**
     * Close a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket closing
     */
    public static SurfFutureResult<Optional<Ticket>> closeTicket(Ticket ticket) {
        Optional<String> ticketIdOptional = ticket.getTicketId();
        if (ticketIdOptional.isEmpty()) {
            return DataApi.getDataInstance().supplyAsync(Optional::empty);
        }

        String checkedTicketId = ticketIdOptional.get();

        return DataApi.getDataInstance().supplyAsync(() -> {
            String url = String.format(API.TICKET, checkedTicketId);

            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket)).build();
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

            ticket.setClosedAt(newTicket.getClosedAt());

            return Optional.of(ticket);
        });
    }

    /**
     * Returns a ticket by the jsonobject
     *
     * @param jsonObject The json object
     * @return The ticket
     */
    @SuppressWarnings("java:S3776")
    public static Ticket ticketByJson(JsonObject jsonObject) {
        Optional<Long> id = Optional.empty();
        Optional<String> ticketId = Optional.empty();
        LocalDateTime openedAt = null;
        Optional<String> guildId = Optional.empty();
        Optional<Guild> guild = Optional.empty();
        Optional<String> channelId = Optional.empty();
        Optional<TextChannel> channel = Optional.empty();
        String ticketTypeString = null;
        TicketType ticketType = null;
        String ticketAuthorName = null;
        String ticketAuthorId = null;
        String ticketAuthorAvatarUrl = null;
        User ticketAuthor = null;
        Optional<String> closedById = Optional.empty();
        Optional<User> closedBy = Optional.empty();
        Optional<String> closedReason = Optional.empty();
        Optional<LocalDateTime> closedAt = Optional.empty();
        List<TicketMessage> messages = new ArrayList<>();
        List<TicketMember> members = new ArrayList<>();

        Optional<Webhook> webhook = Optional.empty();
        Optional<String> webhookId = Optional.empty();
        Optional<String> webhookName = Optional.empty();
        Optional<String> webhookUrl = Optional.empty();

        Ticket ticket = new Ticket(id, ticketId, openedAt, guildId, guild, channelId, channel, ticketTypeString,
                ticketType, ticketAuthorName, ticketAuthorId, ticketAuthorAvatarUrl, ticketAuthor, closedById, closedBy,
                closedReason, closedAt, messages, members, webhook, webhookId, webhookName, webhookUrl);

        if (jsonObject.has("id")) {
            id = Optional.of(jsonObject.get("id").getAsLong());
            ticket.setId(id);
        }

        if (jsonObject.has("ticket_id")) {
            ticketId = Optional.of(jsonObject.get("ticket_id").getAsString());
            ticket.setTicketId(ticketId);
        }

        if (jsonObject.has("opened_at")) {
            openedAt = LocalDateTime.parse(jsonObject.get("opened_at").getAsString().split("\\.")[0]);
            ticket.setOpenedAt(openedAt);
        }

        if (jsonObject.has("guild_id") && jsonObject.get("guild_id") != null && !(jsonObject
                .get("guild_id") instanceof JsonNull)) {
            String guildIdString = jsonObject.get("guild_id").getAsString();
            guildId = Optional.of(guildIdString);

            if (guildIdString != null) {
                guild = Optional.ofNullable(DiscordBot.getInstance().getJda().getGuildById(guildIdString));
                ticket.setGuild(guild);
            }

            ticket.setGuildId(guildId);
        }

        if (jsonObject.has("channel_id") && jsonObject.get("channel_id") != null && !(jsonObject
                .get("channel_id") instanceof JsonNull)) {
            String channelIdString = jsonObject.get("channel_id").getAsString();
            channelId = Optional.of(channelIdString);

            if (channelIdString != null) {
                channel = Optional.ofNullable(DiscordBot.getInstance().getJda().getTextChannelById(channelIdString));
                ticket.setChannel(channel);
            }

            ticket.setChannelId(channelId);
        }

        if (jsonObject.has("type")) {
            ticketTypeString = jsonObject.get("type").getAsString();
            ticketType = TicketType.getByName(ticketTypeString);

            ticket.setTicketType(ticketType);
            ticket.setTicketTypeString(ticketTypeString);
        }

        if (jsonObject.has("author_id") && jsonObject.get("author_id") != null && !(jsonObject
                .get("author_id") instanceof JsonNull)) {
            ticketAuthorId = jsonObject.get("author_id").getAsString();

            if (ticketAuthorId != null) {
                ticket.setTicketAuthor(DiscordBot.getInstance().getJda().retrieveUserById(ticketAuthorId));
            }

            ticket.setTicketAuthorId(ticketAuthorId);
        }

        if (jsonObject.has("author_name") && jsonObject.get("author_name") != null && !(jsonObject
                .get("author_name") instanceof JsonNull)) {
            ticketAuthorName = jsonObject.get("author_name").getAsString();
            ticket.setTicketAuthorName(ticketAuthorName);
        }

        if (jsonObject.has("author_avatar_url") && jsonObject.get("author_avatar_url") != null && !(jsonObject
                .get("author_avatar_url") instanceof JsonNull)) {
            ticketAuthorAvatarUrl = jsonObject.get("author_avatar_url").getAsString();
            ticket.setTicketAuthorAvatarUrl(ticketAuthorAvatarUrl);
        }

        if (jsonObject.has("closed_by_id") && jsonObject.get("closed_by_id") != null && !(jsonObject
                .get("closed_by_id") instanceof JsonNull)) {
            String closedByIdString = jsonObject.get("closed_by_id").getAsString();
            closedById = Optional.of(closedByIdString);

            if (closedByIdString != null) {
                ticket.setClosedBy(Optional.of(DiscordBot.getInstance().getJda().retrieveUserById(closedByIdString)));
            }

            ticket.setClosedById(closedById);
        }

        if (jsonObject.has("closed_reason") && jsonObject.get("closed_reason") != null && !(jsonObject
                .get("closed_reason") instanceof JsonNull)) {
            closedReason = Optional.of(jsonObject.get("closed_reason").getAsString());
            ticket.setClosedReason(closedReason);
        }

        if (jsonObject.has("closed_at") && jsonObject.get("closed_at") != null && !(jsonObject
                .get("closed_at") instanceof JsonNull)) {
            closedAt = Optional.of(LocalDateTime.parse(jsonObject.get("closed_at").getAsString().split("\\.")[0]));
            ticket.setClosedAt(closedAt);
        }

        if (jsonObject.has("webhook_id") && jsonObject.get("webhook_id") != null && !(jsonObject
                .get("webhook_id") instanceof JsonNull)) {
            webhookId = Optional.of(jsonObject.get("webhook_id").getAsString());

            if (channel.isPresent() && webhookId.isPresent()) {
                TextChannel textChannel = channel.get();

                WebhookHelper.getWebhook(textChannel, webhookId.get())
                        .whenComplete(ticket::setWebhook);
            }

            ticket.setWebhookId(webhookId);
        }

        if (jsonObject.has("webhook_name") && jsonObject.get("webhook_name") != null && !(jsonObject
                .get("webhook_name") instanceof JsonNull)) {
            webhookName = Optional.of(jsonObject.get("webhook_name").getAsString());
            ticket.setWebhookName(webhookName);
        }

        if (jsonObject.has("webhook_url") && jsonObject.get("webhook_url") != null && !(jsonObject
                .get("webhook_url") instanceof JsonNull)) {
            webhookUrl = Optional.of(jsonObject.get("webhook_url").getAsString());
            ticket.setWebhookUrl(webhookUrl);
        }

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

            ticket.setMessages(messages);
        }

        if (jsonObject.has("members")) {
            JsonArray membersArray = jsonObject.get("members").getAsJsonArray();

            for (JsonElement memberElement : membersArray) {
                if (!memberElement.isJsonObject()) {
                    continue;
                }

                JsonObject memberObject = memberElement.getAsJsonObject();
                TicketMember member = TicketMember.fromJsonObject(ticket, memberObject);

                members.add(member);
            }

            ticket.setMembers(members);
        }

        return ticket;
    }

    /**
     * Returns a map of the parameters
     *
     * @param ticket The ticket
     * @return The map of the parameters
     */
    public static Map<String, String> toParameters(Ticket ticket) {
        Optional<String> channelId = ticket.getChannelId();
        Optional<String> guildId = ticket.getGuildId();
        String ticketAuthorName = ticket.getTicketAuthorName();
        String ticketAuthorId = ticket.getTicketAuthorId();
        String ticketAuthorAvatarUrl = ticket.getTicketAuthorAvatarUrl();
        String ticketTypeString = ticket.getTicketTypeString();

        Optional<String> closedById = ticket.getClosedById();
        Optional<String> closedReason = ticket.getClosedReason();

        Optional<String> webhookId = ticket.getWebhookId();
        Optional<String> webhookName = ticket.getWebhookName();
        Optional<String> webhookUrl = ticket.getWebhookUrl();

        Map<String, String> parameters = new HashMap<>();

        if (ticketAuthorId != null) {
            parameters.put("author_id", ticketAuthorId);
        }

        if (ticketAuthorName != null) {
            parameters.put("author_name", ticketAuthorName);
        }

        if (ticketAuthorAvatarUrl != null) {
            parameters.put("author_avatar_url", ticketAuthorAvatarUrl);
        }

        if (ticketTypeString != null) {
            parameters.put("type", ticketTypeString);
        }

        if (channelId.isPresent()) {
            parameters.put("channel_id", channelId.get());
        }

        if (guildId.isPresent()) {
            parameters.put("guild_id", guildId.get());
        }

        if (webhookId.isPresent()) {
            parameters.put("webhook_id", webhookId.get());
        }

        if (webhookName.isPresent()) {
            parameters.put("webhook_name", webhookName.get());
        }

        if (webhookUrl.isPresent()) {
            parameters.put("webhook_url", webhookUrl.get());
        }

        if (closedById.isPresent()) {
            parameters.put("closed_by_id", closedById.get());
        }

        if (closedReason.isPresent()) {
            parameters.put("closed_reason", closedReason.get());
        }

        return parameters;
    }

}
