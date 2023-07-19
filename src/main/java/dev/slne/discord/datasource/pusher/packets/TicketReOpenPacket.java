package dev.slne.discord.datasource.pusher.packets;

import dev.slne.data.core.pusher.packet.PusherPacket;
import dev.slne.discord.ticket.Ticket;

public class TicketReOpenPacket extends PusherPacket {

    private Ticket originalTicket;
    private Ticket newTicket;

    /**
     * Create a new instance of the TicketReOpenPacket class.
     */
    public TicketReOpenPacket() {

    }

    /**
     * Create a new instance of the TicketReOpenPacket class.
     *
     * @param originalTicket The original ticket.
     * @param newTicket      The new ticket.
     */
    public TicketReOpenPacket(Ticket originalTicket, Ticket newTicket) {
        this.originalTicket = originalTicket;
        this.newTicket = newTicket;
    }

    /**
     * @return the newTicket
     */
    public Ticket getNewTicket() {
        return newTicket;
    }

    /**
     * @return the originalTicket
     */
    public Ticket getOriginalTicket() {
        return originalTicket;
    }

}
