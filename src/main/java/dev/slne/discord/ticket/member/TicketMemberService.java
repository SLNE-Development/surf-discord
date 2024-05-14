package dev.slne.discord.ticket.member;

import dev.slne.discord.Launcher;
import dev.slne.discord.ticket.Ticket;
import org.springframework.data.util.Lazy;

import java.util.concurrent.CompletableFuture;

/**
 * The type Ticket member service.
 */
public class TicketMemberService {

	public static final TicketMemberService INSTANCE = new TicketMemberService();

	private final Lazy<TicketMemberClient> ticketMemberClient =
			Lazy.of(() -> Launcher.getContext().getBean(TicketMemberClient.class));

	/**
	 * Instantiates a new Ticket member service.
	 */
	private TicketMemberService() {
	}

	/**
	 * Create ticket member completable future.
	 *
	 * @param ticket       the ticket
	 * @param ticketMember the ticket member
	 *
	 * @return the completable future
	 */
	public CompletableFuture<TicketMember> createTicketMember(Ticket ticket, TicketMember ticketMember) {
		return CompletableFuture.supplyAsync(() -> ticketMemberClient.get().createTicketMember(
				ticket.getTicketId(),
				ticketMember
		));
	}

	/**
	 * Update ticket member completable future.
	 *
	 * @param ticket       the ticket
	 * @param ticketMember the ticket member
	 *
	 * @return the completable future
	 */
	public CompletableFuture<TicketMember> updateTicketMember(Ticket ticket, TicketMember ticketMember) {
		return CompletableFuture.supplyAsync(() -> ticketMemberClient.get().updateTicketMember(
				ticket.getTicketId(),
				ticketMember.getMemberId(),
				ticketMember
		));
	}

	/**
	 * Delete ticket member completable future.
	 *
	 * @param ticket       the ticket
	 * @param ticketMember the ticket member
	 *
	 * @return the completable future
	 */
	public CompletableFuture<Void> deleteTicketMember(Ticket ticket, TicketMember ticketMember) {
		return CompletableFuture.runAsync(() -> ticketMemberClient.get().deleteTicketMember(
				ticket.getTicketId(),
				ticketMember.getMemberId()
		));
	}

}
