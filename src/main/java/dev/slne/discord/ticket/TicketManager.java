package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketManager {

    private List<Ticket> tickets;

    /**
     * Constructor for the ticket manager
     */
    public TicketManager() {
        tickets = new ArrayList<>();
    }

    /**
     * Adds a ticket to the ticket manager
     *
     * @param ticket
     */
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    /**
     * Removes a ticket from the ticket manager
     *
     * @param ticket
     */
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    /**
     * Returns a list of all tickets
     *
     * @return A list of all tickets
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

}
