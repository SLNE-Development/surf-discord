package dev.slne.discord.datasource.redis.packets;

import dev.slne.data.api.spring.redis.event.RedisEvent;
import dev.slne.discord.ticket.Ticket;
import lombok.Getter;

/**
 * The type Ticket open packet.
 */
@Getter
public class TicketOpenPacket extends RedisEvent {

	private static final String CHANNEL = "surf:ticket:open";
	private Ticket ticket;

	/**
	 * Create a new instance of the TicketOpenPacket class.
	 */
	public TicketOpenPacket() {

	}

	/**
	 * Create a new instance of the TicketOpenPacket class.
	 *
	 * @param ticket The ticket that was opened.
	 */
	public TicketOpenPacket(Ticket ticket) {
		super(CHANNEL);

		this.ticket = ticket;
	}

}
