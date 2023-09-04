package dev.slne.discord.listener.pusher.ticket;

import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;
import dev.slne.discord.datasource.pusher.packets.TicketReOpenPacket;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;

public class TicketReOpenListener implements Listener {

    @EventHandler
    public void onTicketOpen(DiscordPusherEvent event) {
        if (!(event.packet() instanceof TicketReOpenPacket packet)) {
            return;
        }

        if (packet.getNewTicket() != null) {
            packet.getNewTicket().openFromPusher();
        }
    }

}
