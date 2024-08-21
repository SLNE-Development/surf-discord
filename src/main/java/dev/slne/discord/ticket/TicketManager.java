package dev.slne.discord.ticket;

import dev.slne.data.api.DataApi;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * The type Ticket manager.
 */
@Getter
public class TicketManager {

	private final List<Ticket> ticketQueue;
	private boolean fetched;

	private List<Ticket> tickets;

	/**
	 * Constructor for the ticket manager
	 */
	public TicketManager() {
		fetched = false;
		tickets = new ArrayList<>();
		ticketQueue = new ArrayList<>();
	}

	/**
	 * Fetches all active tickets from the database
	 */
	public void fetchActiveTickets() {
		fetched = false;

		long start = System.currentTimeMillis();

		TicketService.INSTANCE.getActiveTickets().thenAcceptAsync(ticketList -> {
			if (ticketList != null) {
				this.tickets = ticketList;
			}

			long end = System.currentTimeMillis();
			long time = end - start;

			DataApi.getDataInstance()
				   .logInfo(getClass(), String.format("Fetched %d tickets in %dms.", tickets.size(), time));

			fetched = true;
			popQueue();
		}).exceptionally(throwable -> {
			DataApi.getDataInstance().logError(getClass(), "Failed to fetch tickets.", throwable);
			return null;
		});
	}

	/**
	 * Returns a ticket from cache
	 *
	 * @param channelId the channel id
	 *
	 * @return the ticket
	 */
	public Ticket getTicket(String channelId) {
		return getTickets().stream()
					  .filter(ticket -> ticket.getChannelId() != null && ticket.getChannelId().equals(channelId))
					  .findFirst().orElse(null);
	}

	/**
	 * Pops the queue
	 */
	private void popQueue() {
		this.tickets.addAll(ticketQueue);
	}

	/**
	 * Adds a ticket to the ticket manager
	 *
	 * @param ticket The ticket
	 */
	public void addTicket(Ticket ticket) {
		if (!fetched) {
			ticketQueue.add(ticket);
		} else {
			tickets.add(ticket);
		}
	}

	/**
	 * Removes a ticket from the ticket manager
	 *
	 * @param ticket The ticket
	 */
	public void removeTicket(Ticket ticket) {
		tickets.remove(ticket);
	}

	/**
	 * Returns a ticket by its id
	 *
	 * @param ticketId The id of the ticket
	 *
	 * @return The ticket
	 */
	public Ticket getTicketById(UUID ticketId) {
		return getTickets().stream()
				.filter(ticket -> ticket.getTicketId().equals(ticketId))
				.findFirst()
				.orElse(null);
	}

	@Unmodifiable
	public List<Ticket> getTickets() {
		return List.copyOf(tickets);
	}
}
