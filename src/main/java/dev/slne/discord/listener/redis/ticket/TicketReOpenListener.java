package dev.slne.discord.listener.redis.ticket;

import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.datasource.redis.packets.TicketReOpenPacket;

/**
 * The type Ticket re open listener.
 */
@DataListeners
public class TicketReOpenListener {

	/**
	 * On ticket open.
	 *
	 * @param packet the packet
	 */
	@DataListener(channels = "ticket:reopen")
	public void onTicketOpen(TicketReOpenPacket packet) {
		if (packet.getNewTicket() != null) {
			packet.getNewTicket().openFromRedis();
		}
	}

}
