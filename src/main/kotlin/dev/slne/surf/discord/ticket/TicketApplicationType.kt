package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.permission.DiscordPermission

enum class TicketApplicationType(val viewPermission: DiscordPermission) {
    DEVELOPER(DiscordPermission.TICKET_APPLICATION_DEVELOPER),
    DESIGNER(DiscordPermission.TICKET_APPLICATION_DESIGNER),
    BUILDER(DiscordPermission.TICKET_APPLICATION_BUILDER),
    SUPPORTER(DiscordPermission.TICKET_APPLICATION_SUPPORTER),
    TWITCH_MODERATOR(DiscordPermission.TICKET_APPLICATION_TWITCH_MODERATOR)
}