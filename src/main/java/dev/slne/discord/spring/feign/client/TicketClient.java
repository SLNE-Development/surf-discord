package dev.slne.discord.spring.feign.client;

import dev.slne.discord.datasource.API;
import dev.slne.discord.ticket.Ticket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * The interface Ticket member client.
 */
@FeignClient(url = API.API_PREFIX, name = "ticket-service")
public interface TicketClient {

	/**
	 * Add member ticket.
	 *
	 * @param ticket the ticket
	 *
	 * @return the ticket
	 */
	@PostMapping(value = API.TICKETS)
	Ticket createTicket(Ticket ticket);

	/**
	 * Remove member ticket.
	 *
	 * @param ticket the ticket
	 *
	 * @return the ticket
	 */
	@PutMapping(value = API.TICKETS)
	Ticket updateTicket(Ticket ticket);

	/**
	 * Gets tickets.
	 *
	 * @return the tickets
	 */
	@GetMapping(value = API.TICKETS)
	List<Ticket> getTickets();

	/**
	 * Gets tickets.
	 *
	 * @return the tickets
	 */
	@GetMapping(value = API.ACTIVE_TICKETS)
	List<Ticket> getActiveTickets();

}
