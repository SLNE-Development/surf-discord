package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.command.dsl.modal
import it.unimi.dsi.fastutil.objects.ObjectList
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal

enum class TicketType(
    val displayName: String,
    val description: String,
    val emoji: String,
    val closeReasons: ObjectList<TicketCloseReason>,
    val modal: Modal? = null
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
        ),
        modal("modal-whitelist", "Whitelist Anfrage") {
            field {
                id = "whitelist-name"
                label = "Minecraft Name"
                style = TextInputStyle.SHORT
                placeholder = "CastCrafter"
                required = true
                lengthRange = 3..16
            }

            field {
                id = "whitelist-twitch"
                label = "Twitch Name"
                style = TextInputStyle.SHORT
                placeholder = "CastCrafter"
                required = true
            }
        }
    )
}