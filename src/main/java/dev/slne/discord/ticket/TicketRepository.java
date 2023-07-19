package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.slne.data.core.database.future.SurfFutureResult;
import dev.slne.data.core.gson.GsonConverter;
import dev.slne.data.core.instance.DataApi;
import dev.slne.data.core.web.WebRequest;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import dev.slne.discord.datasource.API;
import dev.slne.discord.datasource.database.future.DiscordFutureResult;

public class TicketRepository {

    /**
     * The ticket repository instance
     */
    private TicketRepository() {
    }

    /**
     * Get the ticket by the json object
     *
     * @param jsonObject The json object
     * @return The ticket
     */
    public static Ticket ticketByJson(JsonObject jsonObject) {
        GsonConverter gson = new GsonConverter();

        return gson.fromJson(jsonObject.toString(), Ticket.class);
    }

    /**
     * Get the active tickets
     *
     * @return The active tickets
     */
    public static SurfFutureResult<List<Ticket>> getActiveTickets() {
        CompletableFuture<List<Ticket>> future = new CompletableFuture<>();
        DiscordFutureResult<List<Ticket>> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.ACTIVE_TICKETS).build();

            request.executeGet().thenAccept(response -> {
                List<Ticket> tickets = new ArrayList<>();

                if (response.statusCode() != 200) {
                    Launcher.getLogger(TicketRepository.class).error("Could not get active tickets: {}",
                            response.body());
                    future.complete(tickets);
                    return;
                }

                Object body = response.body();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonArray()) {
                    future.complete(tickets);
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

                future.complete(tickets);
            }).exceptionally(throwable -> {
                Launcher.getLogger(TicketRepository.class).error("Could not get active tickets", throwable);
                future.completeExceptionally(throwable);
                return null;
            });
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
    public static Ticket getTicketByChannel(String channelId) {
        return DiscordBot.getInstance().getTicketManager().getTicket(channelId);
    }

    /**
     * Save a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket saving
     */
    public static SurfFutureResult<Ticket> createTicket(Ticket ticket) {
        CompletableFuture<Ticket> future = new CompletableFuture<>();
        DiscordFutureResult<Ticket> futureResult = new DiscordFutureResult<>(future);

        DataApi.getDataInstance().runAsync(() -> {
            WebRequest request = WebRequest.builder().url(API.TICKETS).json(true).parameters(toParameters(ticket))
                    .build();
            request.executePost().thenAccept(response -> {
                if (response.statusCode() != 201) {
                    Launcher.getLogger(TicketRepository.class).error("Could not create ticket: {}", response.body());
                    future.complete(null);
                    return;
                }

                Object body = response.body();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(null);
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");
                Ticket newTicket = ticketByJson(jsonObject);

                ticket.setOpenedAt(newTicket.getOpenedAt());
                ticket.setTicketId(newTicket.getTicketId());
                ticket.setId(newTicket.getId());
                ticket.setCreatedAt(newTicket.getCreatedAt());

                future.complete(ticket);
            }).exceptionally(throwable -> {
                Launcher.getLogger(TicketRepository.class).error("Could not create ticket", throwable);
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Update a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket updating
     */
    public static SurfFutureResult<Ticket> updateTicket(Ticket ticket) {
        CompletableFuture<Ticket> future = new CompletableFuture<>();
        DiscordFutureResult<Ticket> futureResult = new DiscordFutureResult<>(future);

        String ticketId = ticket.getTicketId();
        if (ticketId == null) {
            future.complete(null);
            return futureResult;
        }

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET, ticketId);
            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket))
                    .build();

            request.executePut().thenAccept(response -> {
                if (!(response.statusCode() == 200 || response.statusCode() == 201)) {
                    Launcher.getLogger(TicketRepository.class).error("Could not update ticket: {}",
                            response.body());
                    future.complete(null);
                    return;
                }

                Object body = response.body();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(null);
                    return;
                }

                future.complete(ticket);
            }).exceptionally(throwable -> {
                Launcher.getLogger(TicketRepository.class).error("Could not update ticket", throwable);
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Close a ticket
     *
     * @param ticket The ticket
     * @return The result of the ticket closing
     */
    public static SurfFutureResult<Ticket> closeTicket(Ticket ticket) {
        CompletableFuture<Ticket> future = new CompletableFuture<>();
        DiscordFutureResult<Ticket> futureResult = new DiscordFutureResult<>(future);

        String ticketId = ticket.getTicketId();
        if (ticketId == null) {
            future.complete(null);
            return futureResult;
        }

        DataApi.getDataInstance().runAsync(() -> {
            String url = String.format(API.TICKET, ticketId);

            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket)).build();
            request.executeDelete().thenAccept(response -> {
                if (response.statusCode() != 200) {
                    Launcher.getLogger(TicketRepository.class).error("Could not close ticket: {}", response.body());
                    future.complete(null);
                    return;
                }

                Object body = response.body();
                String bodyString = body.toString();

                GsonConverter gson = new GsonConverter();
                JsonObject bodyElement = gson.fromJson(bodyString, JsonObject.class);

                if (!bodyElement.has("data") || !bodyElement.get("data").isJsonObject()) {
                    future.complete(null);
                    return;
                }

                JsonObject jsonObject = (JsonObject) bodyElement.get("data");
                Ticket newTicket = ticketByJson(jsonObject);

                ticket.setClosedAt(newTicket.getClosedAt());

                future.complete(ticket);
            }).exceptionally(throwable -> {
                Launcher.getLogger(TicketRepository.class).error("Could not close ticket", throwable);
                future.completeExceptionally(throwable);
                return null;
            });
        });

        return futureResult;
    }

    /**
     * Returns a map of the parameters
     *
     * @param ticket The ticket
     * @return The map of the parameters
     */
    public static Map<String, Object> toParameters(Ticket ticket) {
        String channelId = ticket.getChannelId();
        String guildId = ticket.getGuildId();
        String ticketAuthorName = ticket.getTicketAuthorName();
        String ticketAuthorId = ticket.getTicketAuthorId();
        String ticketAuthorAvatarUrl = ticket.getTicketAuthorAvatarUrl();
        String ticketTypeString = ticket.getTicketTypeString();

        String closedById = ticket.getClosedById();
        String closedReason = ticket.getClosedReason();

        String webhookId = ticket.getWebhookId();
        String webhookName = ticket.getWebhookName();
        String webhookUrl = ticket.getWebhookUrl();

        Map<String, Object> parameters = new HashMap<>();

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

        if (channelId != null) {
            parameters.put("channel_id", channelId);
        }

        if (guildId != null) {
            parameters.put("guild_id", guildId);
        }

        if (webhookId != null) {
            parameters.put("webhook_id", webhookId);
        }

        if (webhookName != null) {
            parameters.put("webhook_name", webhookName);
        }

        if (webhookUrl != null) {
            parameters.put("webhook_url", webhookUrl);
        }

        if (closedById != null) {
            parameters.put("closed_by_id", closedById);
        }

        if (closedReason != null) {
            parameters.put("closed_reason", closedReason);
        }

        return parameters;
    }

}
