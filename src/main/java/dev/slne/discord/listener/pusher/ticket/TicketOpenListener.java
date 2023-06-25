package dev.slne.discord.listener.pusher.ticket;

import dev.slne.discord.DiscordBot;
import dev.slne.discord.datasource.pusher.event.DiscordPusherEvent;
import dev.slne.discord.datasource.pusher.packets.TicketOpenPacket;
import dev.slne.discord.listener.Listener;
import dev.slne.discord.listener.event.EventHandler;

public class TicketOpenListener implements Listener {

    @EventHandler
    public void onTicketOpen(DiscordPusherEvent<TicketOpenPacket> event) {
        if (!(event.getPacket() instanceof TicketOpenPacket)) {
            return;
        }

        TicketOpenPacket packet = event.getPacket();

        System.out.println(packet);

        if (packet.getTicket() != null) {
            packet.getTicket().openFromPusher();

            DiscordBot.getInstance().getTicketManager().addTicket(packet.getTicket());
        }
    }

}
