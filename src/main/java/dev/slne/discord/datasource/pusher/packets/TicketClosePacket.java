package dev.slne.discord.datasource.pusher.packets;

import dev.slne.data.api.spring.redis.event.RedisEvent;
import dev.slne.discord.ticket.Ticket;

public class TicketClosePacket extends RedisEvent {

	private Ticket ticket;

	/**
	 * Creates a new ticket close packet.
	 */
	public TicketClosePacket() {
	}

	/**
	 * Creates a new ticket close packet.
	 *
	 * @param ticket the ticket
	 */
	public TicketClosePacket(Ticket ticket) {
		super("ticket:close");

		this.ticket = ticket;
	}

	/**
	 * @return the ticket
	 */
	public Ticket getTicket() {
		return ticket;
	}

}
