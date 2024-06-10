package dev.slne.discord.datasource.redis.packets;

import dev.slne.data.api.spring.redis.event.RedisEvent;
import dev.slne.discord.ticket.Ticket;
import lombok.Getter;

/**
 * The type Ticket re open packet.
 */
@Getter
public class TicketReOpenPacket extends RedisEvent {

	private Ticket originalTicket;
	private Ticket newTicket;

	/**
	 * Create a new instance of the TicketReOpenPacket class.
	 */
	public TicketReOpenPacket() {

	}

	/**
	 * Create a new instance of the TicketReOpenPacket class.
	 *
	 * @param originalTicket The original ticket.
	 * @param newTicket      The new ticket.
	 */
	public TicketReOpenPacket(Ticket originalTicket, Ticket newTicket) {
		super("ticket:reopen");

		this.originalTicket = originalTicket;
		this.newTicket = newTicket;
	}

}
