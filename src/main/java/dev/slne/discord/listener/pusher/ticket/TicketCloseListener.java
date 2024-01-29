package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.packets.TicketClosePacket;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannel;

@DataListeners
public class TicketCloseListener {

	@DataListener(channels = "ticket:close")
	public void onTicketClose(TicketClosePacket packet) {
		Ticket ticket = packet.getTicket();
		if (ticket == null) {
			return;
		}

		ticket.sendTicketClosedMessages().thenAcceptAsync(v -> {
			TicketChannel.deleteTicketChannel(ticket);
			DiscordBot.getInstance().getTicketManager().removeTicket(ticket);
		}).exceptionally(exception -> {
			DataApi.getDataInstance()
				   .logError(getClass(), "Error while sending ticket close message. Still proceeding to close...",
							 exception
				   );

			TicketChannel.deleteTicketChannel(ticket);
			DiscordBot.getInstance().getTicketManager().removeTicket(ticket);

			return null;
		});
	}

}
