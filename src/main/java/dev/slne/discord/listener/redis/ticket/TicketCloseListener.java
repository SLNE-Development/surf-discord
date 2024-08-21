package dev.slne.discord.listener.redis.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.redis.packets.TicketClosePacket;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannelUtil;

/**
 * The type Ticket close listener.
 */
@DataListeners
public class TicketCloseListener {

	/**
	 * On ticket close.
	 *
	 * @param packet the packet
	 */
	@DataListener(channels = "ticket:close")
	public void onTicketClose(TicketClosePacket packet) {
		Ticket ticket = packet.getTicket();
		if (ticket == null) {
			return;
		}

		ticket.sendTicketClosedMessages().thenAcceptAsync(v -> {
			TicketChannelUtil.deleteTicketChannel(ticket);
			DiscordBot.getInstance().getTicketManager().removeTicket(ticket);
		}).exceptionally(exception -> {
			DataApi.getDataInstance()
				   .logError(getClass(), "Error while sending ticket close message. Still proceeding to close...",
							 exception
				   );

			TicketChannelUtil.deleteTicketChannel(ticket);
			DiscordBot.getInstance().getTicketManager().removeTicket(ticket);

			return null;
		});
	}

}
