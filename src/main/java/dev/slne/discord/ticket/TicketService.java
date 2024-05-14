package dev.slne.discord.ticket;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.Launcher;
import org.springframework.data.util.Lazy;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket service.
 */
public class TicketService {

	/**
	 * The constant INSTANCE.
	 */
	public static final TicketService INSTANCE = new TicketService();

	private final Lazy<TicketClient> ticketClient = Lazy.of(() -> Launcher.getContext().getBean(TicketClient.class));

	/**
	 * The ticket repository instance
	 */
	private TicketService() {
	}

	/**
	 * Get the active tickets
	 *
	 * @return The active tickets
	 */
	public CompletableFuture<List<Ticket>> getActiveTickets() {
		return CompletableFuture.supplyAsync(() -> ticketClient.get().getActiveTickets());
	}

	/**
	 * Get the ticket by the id
	 *
	 * @param channelId The channel id
	 *
	 * @return The ticket
	 */
	public Ticket getTicketByChannel(String channelId) {
		return DiscordBot.getInstance().getTicketManager().getTicket(channelId);
	}

	/**
	 * Save a ticket
	 *
	 * @param ticket The ticket
	 *
	 * @return The result of the ticket saving
	 */
	public CompletableFuture<Ticket> createTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() -> {
			Ticket newTicket = ticketClient.get().createTicket(ticket);

			ticket.setOpenedAt(newTicket.getOpenedAt());
			ticket.setTicketId(newTicket.getTicketId());
			ticket.setId(newTicket.getId());
			ticket.setCreatedAt(newTicket.getCreatedAt());

			future.complete(ticket);
		}).exceptionally(exception -> {
			future.completeExceptionally(exception);
			return null;
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
	public CompletableFuture<Ticket> updateTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		UUID ticketId = ticket.getTicketId();

		if (ticketId == null) {
			future.completeExceptionally(new IllegalArgumentException("Ticket id is null"));
			return future;
		}

		return CompletableFuture.supplyAsync(() -> ticketClient.get().updateTicket(ticket));
	}

	/**
	 * Close a ticket
	 *
	 * @param ticket The ticket
	 *
	 * @return The result of the ticket closing
	 */
	public CompletableFuture<Ticket> closeTicket(Ticket ticket) {
		CompletableFuture<Ticket> future = new CompletableFuture<>();

		UUID ticketId = ticket.getTicketId();

		if (ticketId == null) {
			future.complete(null);
			return future;
		}

		CompletableFuture.runAsync(() -> {
			Ticket closedTicket = ticketClient.get().updateTicket(ticket);

			ticket.setClosedAt(closedTicket.getClosedAt());

			future.complete(ticket);
		}).exceptionally(exception -> {
			future.completeExceptionally(exception);
			return null;
		});

		return future;
	}

}
