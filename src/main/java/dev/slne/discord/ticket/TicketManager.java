package dev.slne.discord.ticket;

import java.util.ArrayList;
import java.util.List;

import dev.slne.discord.Launcher;

public class TicketManager {

    private boolean fetched;
    private List<Ticket> tickets;
    private List<Ticket> ticketQueue;

    /**
     * Constructor for the ticket manager
     */
    public TicketManager() {
        fetched = false;
        tickets = new ArrayList<>();
        ticketQueue = new ArrayList<>();
    }

    /**
     * Fetches all active tickets from the database
     */
    public void fetchActiveTickets() {
        fetched = false;

        long start = System.currentTimeMillis();

        TicketRepository.getActiveTickets().whenComplete(ticketList -> {
            if (ticketList != null) {
                this.tickets = ticketList;
            }

            long end = System.currentTimeMillis();
            long time = end - start;

            Launcher.getLogger(getClass()).info("Fetched {} tickets in {}ms.", tickets.size(), time);

            fetched = true;
            popQueue();
        });
    }

    /**
     * Returns a ticket from cache
     *
     * @param channelId the channel id
     * @return the ticket
     */
    public Ticket getTicket(String channelId) {
        return tickets.stream()
                .filter(ticket -> ticket.getChannelId() != null && ticket.getChannelId().equals(channelId))
                .findFirst().orElse(null);
    }

    /**
     * Pops the queue
     */
    private void popQueue() {
        this.tickets.addAll(ticketQueue);
    }

    /**
     * Adds a ticket to the ticket manager
     *
     * @param ticket
     */
    public void addTicket(Ticket ticket) {
        if (!fetched) {
            ticketQueue.add(ticket);
        } else {
            tickets.add(ticket);
        }
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

    /**
     * @return the ticketQueue
     */
    public List<Ticket> getTicketQueue() {
        return ticketQueue;
    }

    /**
     * @return the fetched
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * Returns a ticket by its id
     *
     * @param ticketId The id of the ticket
     * @return The ticket
     */
    public Ticket getTicketById(long ticketId) {
        return tickets.stream().filter(ticket -> ticket.getId() == ticketId).findFirst().orElse(null);
    }
}
