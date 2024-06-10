package dev.slne.discord.datasource.redis.packets;

import dev.slne.data.api.spring.redis.event.RedisEvent;
import dev.slne.discord.ticket.Ticket;
import lombok.Getter;

/**
 * The type Ticket close packet.
 */
@Getter
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

}
