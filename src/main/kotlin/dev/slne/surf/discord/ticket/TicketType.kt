package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.permission.DiscordPermission
import it.unimi.dsi.fastutil.objects.ObjectList
import net.dv8tion.jda.api.interactions.modals.Modal

enum class TicketType(
    val id: String,
    val displayName: String,
    val description: String,
    val emoji: String,
    val viewPermission: DiscordPermission,
    val closeReasons: ObjectList<TicketCloseReason>,
    val modal: Modal? = null
) {
    WHITELIST(
        "whitelist",
        "Whitelist Ticket",
        "Erstelle ein Ticket, um auf dem Survival Server gewhitelisted zu werden.",
        "📜",
        DiscordPermission.TICKET_WHITELIST_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Anforderungen nicht erfüllt",
                "Du erfüllst nicht alle Anforderungen für eine Whitelist."
            )
        ),
        getBean<ModalRegistry>().get("ticket:whitelist").create()
    ),
    SUPPORT_DISCORD(
        "discord",
        "Discord Support Ticket",
        "Erstelle ein Ticket, um Support für den Discord Server zu erhalten.",
        "💬",
        DiscordPermission.TICKET_SUPPORT_DISCORD_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Erledigt",
                "Dein Anliegen wurde bearbeitet."
            )
        ),
        getBean<ModalRegistry>().get("ticket:support:discord").create()
    ),
    SUPPORT_SURVIVAL(
        "survival",
        "Survival Support Ticket",
        "Erstelle ein Ticket, um Support für den Survival Server zu erhalten.",
        "🛠️",
        DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Erledigt",
                "Dein Anliegen wurde bearbeitet."
            )
        )
    ),
    SUPPORT_EVENT(
        "event",
        "Event Support Ticket",
        "Erstelle ein Ticket, um Support für Events zu erhalten.",
        "🎉",
        DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Erledigt",
                "Dein Anliegen wurde bearbeitet."
            )
        )
    ),
    REPORT(
        "report",
        "Report Ticket",
        "Erstelle ein Ticket, um einen Spieler zu melden.",
        "🚨",
        DiscordPermission.TICKET_REPORT_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Fall abgeschlossen",
                "Der gemeldete Fall wurde abgeschlossen."
            )
        )
    ),
    UNBAN(
        "unban",
        "Unban Antrag",
        "Erstelle ein Ticket, um einen Unban Antrag zu stellen.",
        "🔨",
        DiscordPermission.TICKET_UNBAN_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Antrag abgelehnt",
                "Dein Unban Antrag wurde abgelehnt."
            ),
            TicketCloseReason.of(
                "Ban aufgehoben",
                "Du wurdest entbannt. Bitte mache dich erneut mit unserem Regelwerk vertraut."
            ),
            TicketCloseReason.of(
                "Ban verkürzt",
                "Dein Ban wurde verkürzt."
            )
        )
    ),
    BUGREPORT(
        "bugreport",
        "Bugreport Ticket",
        "Erstelle ein Ticket, um einen Bug zu melden.",
        "🐛",
        DiscordPermission.TICKET_BUG_VIEW,
        ObjectList.of(
            TicketCloseReason.of(
                "Bug bestätigt",
                "Der gemeldete Bug wurde bestätigt und an das Entwicklungsteam weitergeleitet."
            ),
            TicketCloseReason.of(
                "Bug nicht reproduzierbar",
                "Der Fehler konnte nicht reproduziert werden. Bitte stelle sicher, dass du alle notwendigen Informationen bereitgestellt hast. Sollte dieser erneut auftreten, öffne bitte ein neues Ticket."
            ),
            TicketCloseReason.of(
                "Bug behoben",
                "Der gemeldete Fehler wurde behoben. Danke für deinen Bugreport!"
            )
        )
    )
}