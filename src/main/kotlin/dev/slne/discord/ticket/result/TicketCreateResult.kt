package dev.slne.discord.ticket.result

enum class TicketCreateResult {
    SUCCESS,
    ALREADY_EXISTS,
    GUILD_NOT_FOUND,
    GUILD_CONFIG_NOT_FOUND,
    CATEGORY_NOT_FOUND,
    ERROR,
    MISSING_PERMISSIONS
}
