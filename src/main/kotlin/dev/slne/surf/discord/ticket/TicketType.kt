package dev.slne.surf.discord.ticket

import it.unimi.dsi.fastutil.objects.ObjectList

enum class TicketType(
    val displayName: String,
    val description: String,
    val emoji: String,
    val closeReasons: ObjectList<TicketCloseReason>
) {
    WHITELIST(
        "Whitelist Ticket",
        "Erstelle ein Ticket, um auf dem Survival Server gewhitelisted zu werden.",
        "📜",
        ObjectList.of(
            TicketCloseReason.of(
                "Anforderungen nicht erfüllt",
                "Du erfüllst nicht alle Anforderungen für eine Whitelist."
            ),
            TicketCloseReason.of(
                "Erfolgreich gewhitelisted",
                "Du befindest dich nun auf der Whitelist."
            )
        )
    )
}