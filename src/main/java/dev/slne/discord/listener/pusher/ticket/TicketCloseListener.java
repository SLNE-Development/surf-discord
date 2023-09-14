package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.DataApi;
import dev.slne.data.api.event.Subscribe;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.packets.TicketClosePacket;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannel;

@SuppressWarnings("unused")
public class TicketCloseListener {

    @Subscribe
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
                            exception);

            TicketChannel.deleteTicketChannel(ticket);
            DiscordBot.getInstance().getTicketManager().removeTicket(ticket);

            return null;
        });
    }

}
