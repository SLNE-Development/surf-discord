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
     * The ticket repository had an error
     */
    TICKET_REPOSITORY_ERROR,

    /**
     * The ticket channel is not closable
     */
    TICKET_CHANNEL_NOT_CLOSABLE,

    /**
     * There was an error while closing the ticket
     */
    ERROR;
}
