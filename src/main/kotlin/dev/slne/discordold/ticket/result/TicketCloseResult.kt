package dev.slne.discordold.ticket.result

enum class TicketCloseResult {
    SUCCESS,
    TICKET_NOT_FOUND,
    TICKET_ALREADY_CLOSING,
    TICKET_ALREADY_CLOSED,
    TICKET_CHANNEL_NOT_CLOSABLE,
    ERROR
}
