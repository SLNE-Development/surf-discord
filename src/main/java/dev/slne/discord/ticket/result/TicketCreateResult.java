package dev.slne.discord.ticket.result;

public enum TicketCreateResult {

    /**
     * The ticket was created successfully
     */
    SUCCESS,

    /**
     * The ticket already exists
     */
    ALREADY_EXISTS,

    /**
     * The guild was not found
     */
    GUILD_NOT_FOUND,

    /**
     * The category was not found
     */
    CATEGORY_NOT_FOUND,

    /**
     * The channel could not be created
     */
    ERROR;

}
