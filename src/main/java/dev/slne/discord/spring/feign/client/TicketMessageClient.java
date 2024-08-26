package dev.slne.discord.spring.feign.client;

import dev.slne.discord.datasource.API;
import dev.slne.discord.ticket.message.TicketMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

/**
 * The interface Ticket message client.
 */
@FeignClient(name = "ticket-message-service", url = API.API_PREFIX)
public interface TicketMessageClient {

	/**
	 * Create ticket message ticket message.
	 *
	 * @param ticketId      the ticket id
	 * @param ticketMessage the ticket message
	 *
	 * @return the ticket message
	 */
	@PostMapping(API.TICKET_MESSAGES)
  TicketMessage createTicketMessage(@PathVariable UUID ticketId, TicketMessage ticketMessage);
}
