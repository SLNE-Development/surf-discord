package dev.slne.discord.ticket;

import com.google.gson.JsonObject;
import dev.slne.data.api.gson.GsonConverter;
import dev.slne.discord.DiscordBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
	 *
	 * @return The ticket
	 */
	public static Ticket ticketByJson(JsonObject jsonObject) {
		GsonConverter gson = DiscordBot.getInstance().getGsonConverter();

		return gson.fromJson(jsonObject.toString(), Ticket.class);
	}

	/**
	 * Get the active tickets
	 *
	 * @return The active tickets
	 */
	public static CompletableFuture<List<Ticket>> getActiveTickets() {
		CompletableFuture<List<Ticket>> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
//            WebRequest request = WebRequest.builder().url(API.ACTIVE_TICKETS).build();
//
//            request.executeGet().thenAccept(response -> {
//                List<Ticket> tickets = new ArrayList<>();
//                JsonArray dataArray = response.bodyArray(DiscordBot.getInstance().getGsonConverter());
//
//                for (JsonElement ticketElement : dataArray) {
//                    if (!ticketElement.isJsonObject()) {
//                        continue;
//                    }
//
//                    JsonObject ticketObject = ticketElement.getAsJsonObject();
//                    Ticket ticket = ticketByJson(ticketObject);
//
//                    tickets.add(ticket);
//                }
//
//                future.complete(tickets);
//            }).exceptionally(throwable -> {
//                DataApi.getDataInstance().logError(TicketRepository.class, "Could not get active tickets", throwable);
//                future.completeExceptionally(throwable);
//                return null;
//            }); // FIXME: 28.01.2024 00:05 feign
		});

		return future;
	}

	/**
	 * Get the ticket by the id
	 *
	 * @param channelId The channel id
	 *
	 * @return The ticket
	 */
	public static Ticket getTicketByChannel(String channelId) {
		return DiscordBot.getInstance().getTicketManager().getTicket(channelId);
	}

	/**
	 * Save a ticket
	 *
	 * @param ticket The ticket
	 *
	 * @return The result of the ticket saving
	 */
	public static CompletableFuture<Ticket> createTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
//            WebRequest request = WebRequest.builder().url(API.TICKETS).json(true).parameters(toParameters(ticket))
//                    .build();
//            request.executePost().thenAccept(response -> {
//                JsonObject jsonObject = response.bodyObject(DiscordBot.getInstance().getGsonConverter());
//                Ticket newTicket = ticketByJson(jsonObject);
//
//                ticket.setOpenedAt(newTicket.getOpenedAt());
//                ticket.setTicketId(newTicket.getTicketId());
//                ticket.setId(newTicket.getId());
//                ticket.setCreatedAt(newTicket.getCreatedAt());
//
//                future.complete(ticket);
//            }).exceptionally(throwable -> {
//                DataApi.getDataInstance().logError(TicketRepository.class, "Could not create ticket", throwable);
//                future.completeExceptionally(throwable);
//                return null;
//            }); // FIXME: 28.01.2024 00:05 feign
		});

		return future;
	}

	/**
	 * Update a ticket
	 *
	 * @param ticket The ticket
	 *
	 * @return The result of the ticket updating
	 */
	public static CompletableFuture<Ticket> updateTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		String ticketId = ticket.getTicketId();
		if (ticketId == null) {
			future.complete(null);
			return future;
		}

		CompletableFuture.runAsync(() -> {
//            String url = String.format(API.TICKET, ticketId);
//            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket))
//                    .build();
//
//            request.executePut().thenAccept(response -> {
//                ticketByJson(response.bodyObject(DiscordBot.getInstance().getGsonConverter()));
//
//                future.complete(ticket);
//            }).exceptionally(throwable -> {
//                DataApi.getDataInstance().logError(TicketRepository.class, "Could not update ticket", throwable);
//                future.completeExceptionally(throwable);
//                return null;
//            }); // FIXME: 28.01.2024 00:05 feign
		});

		return future;
	}

	/**
	 * Close a ticket
	 *
	 * @param ticket The ticket
	 *
	 * @return The result of the ticket closing
	 */
	public static CompletableFuture<Ticket> closeTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		String ticketId = ticket.getTicketId();
		if (ticketId == null) {
			future.complete(null);
			return future;
		}

		CompletableFuture.runAsync(() -> {
//            String url = String.format(API.TICKET, ticketId);
//
//            WebRequest request = WebRequest.builder().url(url).json(true).parameters(toParameters(ticket)).build();
//            request.executeDelete().thenAccept(response -> {
//                Ticket newTicket = ticketByJson(response.bodyObject(DiscordBot.getInstance().getGsonConverter()));
//
//                ticket.setClosedAt(newTicket.getClosedAt());
//
//                future.complete(ticket);
//            }).exceptionally(throwable -> {
//                DataApi.getDataInstance().logError(TicketRepository.class, "Could not close ticket", throwable);
//                future.completeExceptionally(throwable);
//                return null;
//            }); // FIXME: 28.01.2024 00:05 feign
		});

		return future;
	}

	/**
	 * Returns a map of the parameters
	 *
	 * @param ticket The ticket
	 *
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
