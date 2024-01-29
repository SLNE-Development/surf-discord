package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.datasource.pusher.packets.TicketReOpenPacket;

@DataListeners
public class TicketReOpenListener {

	@DataListener(channels = "ticket:reopen")
	public void onTicketOpen(TicketReOpenPacket packet) {
		if (packet.getNewTicket() != null) {
			packet.getNewTicket().openFromPusher();
		}
	}

}
