package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.packets.TicketOpenPacket;

@DataListeners
public class TicketOpenListener {

	@DataListener(channels = "ticket:open")
	public void onTicketOpen(TicketOpenPacket packet) {
		if (packet.getTicket() != null) {
			packet.getTicket().openFromPusher();

			DiscordBot.getInstance().getTicketManager().addTicket(packet.getTicket());
		}
	}

}
