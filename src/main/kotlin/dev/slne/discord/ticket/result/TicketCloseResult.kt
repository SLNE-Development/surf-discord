package dev.slne.discord.ticket.result

enum class TicketCloseResult {
    SUCCESS,
    TICKET_NOT_FOUND,
    TICKET_REPOSITORY_ERROR,
    TICKET_CHANNEL_NOT_CLOSABLE,
    ERROR
}
