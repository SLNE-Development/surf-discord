package dev.slne.discord.ticket;

import dev.slne.discord.TestTicket;
import dev.slne.discord.datasource.API;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * The interface Ticket member client.
 */
@Service
@FeignClient(url = API.API_PREFIX, name = "ticket-client")
public interface TicketClient {

	/**
	 * Add member ticket.
	 *
	 * @param ticket the ticket
	 *
	 * @return the ticket
	 */
	@PostMapping(value = API.TICKETS)
	TestTicket createTicket(TestTicket ticket);

	/**
	 * Remove member ticket.
	 *
	 * @param ticket the ticket
	 *
	 * @return the ticket
	 */
	@PutMapping(value = API.TICKETS)
	TestTicket updateTicket(TestTicket ticket);

	/**
	 * Gets tickets.
	 *
	 * @return the tickets
	 */
	@GetMapping(value = API.TICKETS)
	TestTicket[] getTickets();

	/**
	 * Gets tickets.
	 *
	 * @return the tickets
	 */
	@GetMapping(value = API.ACTIVE_TICKETS)
	List<TestTicket> getActiveTickets();

}
