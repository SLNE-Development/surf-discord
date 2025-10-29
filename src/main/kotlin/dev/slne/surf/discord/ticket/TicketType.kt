package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import it.unimi.dsi.fastutil.objects.ObjectList
import net.dv8tion.jda.api.interactions.modals.Modal

enum class TicketType(
    val id: String,
    val displayName: String,
    val description: String,
    val emoji: String,
    val closeReasons: ObjectList<TicketCloseReason>,
    val modal: Modal? = null
) {
    WHITELIST(
        "whitelist",
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
        ),
        getBean<ModalRegistry>().get("ticket:whitelist").modal
    )
}