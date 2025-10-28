package dev.slne.discordold.settings

import net.dv8tion.jda.api.requests.GatewayIntent

object GatewayIntents {

    val gatewayIntents = listOf(
        // Emoji
        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,

        // Guild
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.SCHEDULED_EVENTS,

        // Guild Messages
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGE_TYPING,

        // Direct Messages
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING
    )
}
