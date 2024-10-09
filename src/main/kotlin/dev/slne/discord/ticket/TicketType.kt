package dev.slne.discord.ticket

import dev.slne.discord.guild.permission.DiscordPermission
import net.dv8tion.jda.api.entities.emoji.Emoji

enum class TicketType(
    val displayName: String,
    val configName: String,
    val description: String,
    val emoji: Emoji,
    val viewPermission: DiscordPermission
) {
    WHITELIST(
        "WhitelistDTO",
        "whitelist",
        "WhitelistDTO Anfragen für den Survival Server",
        Emoji.fromUnicode("\uD83D\uDCDC"),
        DiscordPermission.VIEW_WHITELIST_TICKETS
    ),

    SURVIVAL_SUPPORT(
        "Survival Support",
        "survival-support",
        "Anliegen bezüglich des Survival Servers",
        Emoji.fromUnicode("\uD83D\uDEE0\uFE0F"),
        DiscordPermission.VIEW_SURVIVAL_SUPPORT_TICKETS
    ),

    EVENT_SUPPORT(
        "Event Support",
        "event-support",
        "Anliegen bezüglich des Event Servers",
        Emoji.fromUnicode("\uD83C\uDF89"),
        DiscordPermission.VIEW_EVENT_SUPPORT_TICKETS
    ),

    BUGREPORT(
        "Bug Report",
        "bugreport",
        "Fehler gefunden? Melde ihn hier.",
        Emoji.fromUnicode("\uD83D\uDC1E"),
        DiscordPermission.VIEW_BUGREPORT_TICKETS
    ),

    REPORT(
        "Report",
        "report",
        "Melde Griefing, Cheating oder andere Regelverstöße.",
        Emoji.fromUnicode("📢"),
        DiscordPermission.VIEW_REPORT_TICKETS
    ),

    UNBAN(
        "Entbannungsantrag",
        "unban",
        "Entbannungsanträge für den Community Server",
        Emoji.fromUnicode("\uD83D\uDEAB"),
        DiscordPermission.VIEW_UNBAN_TICKETS
    ),

    DISCORD_SUPPORT(
        "Discord Support",
        "discord-support",
        "Anliegen bezüglich des Discord Servers",
        Emoji.fromUnicode("\uD83D\uDCAC"),
        DiscordPermission.VIEW_DISCORD_SUPPORT_TICKETS
    );
}

fun getTicketTypeByDisplayName(name: String) = TicketType.entries.find { it.displayName == name }
