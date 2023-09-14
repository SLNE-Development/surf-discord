package dev.slne.discord.listener.pusher.ticket;

import dev.slne.data.api.event.Subscribe;
import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.packets.TicketOpenPacket;

public class TicketOpenListener {

    @Subscribe
    public void onTicketOpen(TicketOpenPacket packet) {
        if (packet.getTicket() != null) {
            packet.getTicket().openFromPusher();

            DiscordBot.getInstance().getTicketManager().addTicket(packet.getTicket());
        }
    }

}
