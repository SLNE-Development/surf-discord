package dev.slne.discord.ticket.result;

public enum TicketMemberRemoveResult {

    /**
     * The user was removed successfully
     */
    SUCCESS,

    /**
     * The user was not found
     */
    USER_NOT_FOUND,

    /**
     * The user was not added
     */
    ERROR

}
