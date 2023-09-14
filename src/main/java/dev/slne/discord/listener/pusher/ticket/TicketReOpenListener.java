package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.event.Subscribe;
import dev.slne.discord.datasource.pusher.packets.TicketReOpenPacket;

public class TicketReOpenListener {

    @Subscribe
    public void onTicketOpen(TicketReOpenPacket packet) {
        if (packet.getNewTicket() != null) {
            packet.getNewTicket().openFromPusher();
        }
    }

}
