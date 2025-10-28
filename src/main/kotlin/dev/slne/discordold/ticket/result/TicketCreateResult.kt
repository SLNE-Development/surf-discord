package dev.slne.discordold.ticket.result

enum class TicketCreateResult {
    SUCCESS,
    ALREADY_EXISTS,
    GUILD_NOT_FOUND,
    GUILD_CONFIG_NOT_FOUND,
    CHANNEL_NOT_FOUND,
    ERROR,
    MISSING_PERMISSIONS,
    AUTHOR_NOT_FOUND,
    ROLE_NOT_FOUND,
}
