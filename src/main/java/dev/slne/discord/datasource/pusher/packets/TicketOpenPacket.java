package dev.slne.discord.datasource.pusher.packets;

import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.ticket.Ticket;

public class TicketOpenPacket extends PusherPacket {

    private Ticket ticket;

    /**
     * Create a new instance of the TicketOpenPacket class.
     */
    public TicketOpenPacket() {

    }

    /**
     * Create a new instance of the TicketOpenPacket class.
     *
     * @param ticket The ticket that was opened.
     */
    public TicketOpenPacket(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * @return the ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

}
