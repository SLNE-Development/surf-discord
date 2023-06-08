package dev.slne.discord.ticket.result;

public enum TicketCloseResult {

    /**
     * The ticket was closed successfully
     */
    SUCCESS,

    /**
     * The ticket was not found
     */
    TICKET_NOT_FOUND,

    /**
     * There was an error while closing the ticket
     */
    ERROR;
}
