package dev.slne.discord.listener.pusher.ticket;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;
import dev.slne.discord.datasource.pusher.packets.TicketClosePacket;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannel;

public class TicketCloseListener implements Listener {

    @EventHandler
    public void onTicketClose(DiscordPusherEvent<TicketClosePacket> event) {
        if (!(event.getPacket() instanceof TicketClosePacket)) {
            return;
        }

        TicketClosePacket packet = event.getPacket();
        Ticket ticket = packet.getTicket();

        if (ticket == null) {
            return;
        }

        TicketChannel.deleteTicketChannel(ticket);
        DiscordBot.getInstance().getTicketManager().removeTicket(ticket);
    }

}
