package dev.slne.discord.ticket

import dev.slne.discord.guild.permission.TicketViewPermission
import net.dv8tion.jda.api.entities.emoji.Emoji

enum class TicketType(
    val displayName: String,
    val configName: String,
    val description: String,
    val emoji: Emoji,
    val viewPermission: TicketViewPermission,
    val shouldPrintWlQuery: Boolean = true,
    val enabled: Boolean = true,
) {
    WHITELIST(
        "WhitelistDTO",
        "whitelist",
        "WhitelistDTO Anfragen für den Survival Server",
        Emoji.fromUnicode("\uD83D\uDCDC"),
        TicketViewPermission.VIEW_WHITELIST_TICKETS,
        shouldPrintWlQuery = false
    ),

    SURVIVAL_SUPPORT(
        "Survival Support",
        "survival-support",
        "Anliegen bezüglich des Survival Servers",
        Emoji.fromUnicode("\uD83D\uDEE0\uFE0F"),
        TicketViewPermission.VIEW_SURVIVAL_SUPPORT_TICKETS,
    ),

    EVENT_SUPPORT(
        "Event Support",
        "event-support",
        "Anliegen bezüglich des Event Servers",
        Emoji.fromUnicode("\uD83C\uDF89"),
        TicketViewPermission.VIEW_EVENT_SUPPORT_TICKETS
    ),

    BUGREPORT(
        "Bug Report",
        "bugreport",
        "Fehler gefunden? Melde ihn hier.",
        Emoji.fromUnicode("\uD83D\uDC1E"),
        TicketViewPermission.VIEW_BUGREPORT_TICKETS
    ),

    REPORT(
        "Report",
        "report",
        "Melde Griefing, Cheating oder andere Regelverstöße.",
        Emoji.fromUnicode("📢"),
        TicketViewPermission.VIEW_REPORT_TICKETS
    ),

    UNBAN(
        "Entbannungsantrag",
        "unban",
        "Entbannungsanträge für den Community Server",
        Emoji.fromUnicode("\uD83D\uDEAB"),
        TicketViewPermission.VIEW_UNBAN_TICKETS
    ),

    DISCORD_SUPPORT(
        "Discord Support",
        "discord-support",
        "Anliegen bezüglich des Discord Servers",
        Emoji.fromUnicode("\uD83D\uDCAC"),
        TicketViewPermission.VIEW_DISCORD_SUPPORT_TICKETS,
        shouldPrintWlQuery = false
    ),

    APPLICATION(
        "Bewerbung",
        "application",
        "Bewirb dich hier für ein Rang",
        Emoji.fromUnicode("\uD83D\uDC68\u200D\uD83D\uDCBB"),
        TicketViewPermission.VIEW_APPLICATION_TICKETS,
        shouldPrintWlQuery = false
    )
}

fun getTicketTypeByDisplayName(name: String) = TicketType.entries.find { it.displayName == name }

fun getTicketTypeByConfigName(name: String) = TicketType.entries.find { it.configName == name }