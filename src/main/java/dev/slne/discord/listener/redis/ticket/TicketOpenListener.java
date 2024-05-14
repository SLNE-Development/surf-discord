package dev.slne.discord.listener.redis.ticket;

import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.redis.packets.TicketOpenPacket;

/**
 * The type Ticket open listener.
 */
@DataListeners
public class TicketOpenListener {

	/**
	 * On ticket open.
	 *
	 * @param packet the packet
	 */
	@DataListener(channels = "ticket:open")
	public void onTicketOpen(TicketOpenPacket packet) {
		if (packet.getTicket() != null) {
			packet.getTicket().openFromRedis();

			DiscordBot.getInstance().getTicketManager().addTicket(packet.getTicket());
		}
	}

}
