package dev.slne.discord.datasource.pusher.packets;

import dev.slne.data.api.pusher.packet.PusherPacket;
import dev.slne.discord.ticket.Ticket;

public class TicketClosePacket extends PusherPacket {

    private Ticket ticket;

    /**
     * Creates a new ticket close packet.
     */
    public TicketClosePacket() {
    }

    /**
     * Creates a new ticket close packet.
     *
     * @param ticket the ticket
     */
    public TicketClosePacket(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

}
