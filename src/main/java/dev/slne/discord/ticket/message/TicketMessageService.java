package dev.slne.discord.ticket.message;

import dev.slne.discord.Launcher;
import dev.slne.discord.ticket.Ticket;
import org.springframework.data.util.Lazy;

import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket message service.
 */
public class TicketMessageService {

	public static TicketMessageService INSTANCE = new TicketMessageService();

	private final Lazy<TicketMessageClient> ticketMessageClient =
			Lazy.of(() -> Launcher.getContext().getBean(TicketMessageClient.class));

	/**
	 * Instantiates a new Ticket message service.
	 */
	private TicketMessageService() {
	}

	/**
	 * Create ticket message completable future.
	 *
	 * @param ticket        the ticket
	 * @param ticketMessage the ticket message
	 *
	 * @return the completable future
	 */
	public CompletableFuture<TicketMessage> createTicketMessage(Ticket ticket, TicketMessage ticketMessage) {
		return CompletableFuture.supplyAsync(() -> ticketMessageClient.get().createTicketMessage(ticket.getTicketId()
				, ticketMessage));
	}
}
