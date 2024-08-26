package dev.slne.discord.spring.feign.client;

import dev.slne.discord.datasource.API;
import dev.slne.discord.ticket.member.TicketMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

/**
 * The interface Ticket member client.
 */
@FeignClient(name = "ticket-member-client", url = API.API_PREFIX)
public interface TicketMemberClient {

	/**
	 * Create ticket member ticket member.
	 *
	 * @param ticketId     the ticket id
	 * @param ticketMember the ticket member
	 *
	 * @return the ticket member
	 */
	@PostMapping(API.TICKET_MEMBERS)
	TicketMember createTicketMember(@PathVariable UUID ticketId, TicketMember ticketMember);

	/**
	 * Update ticket member ticket member.
	 *
	 * @param ticketId     the ticket id
	 * @param memberId     the member id
	 * @param ticketMember the ticket member
	 *
	 * @return the ticket member
	 */
	@PutMapping(API.TICKET_MEMBER)
	TicketMember updateTicketMember(
			@PathVariable UUID ticketId, @PathVariable String memberId, TicketMember ticketMember
	);

	/**
	 * Delete ticket member.
	 *
	 * @param ticketId the ticket id
	 * @param memberId the member id
	 */
	@DeleteMapping(API.TICKET_MEMBER)
	void deleteTicketMember(@PathVariable UUID ticketId, @PathVariable String memberId);

}
