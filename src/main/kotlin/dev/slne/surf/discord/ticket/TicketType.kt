package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import net.dv8tion.jda.api.modals.Modal

private val defaultReasons = mutableObjectListOf(
    TicketCloseReason.of(
        "Anliegen bearbeitet",
        "Dein Anliegen wurde bearbeitet."
    ),
    TicketCloseReason.of(
        "Falscher Typ",
        "Du hast ein Ticket des falschen Typs erstellt."
    ),
    TicketCloseReason.of(
        "Spam / Missbrauch",
        "Das Ticket wurde als Spam oder Missbrauch eingestuft."
    ),
    TicketCloseReason.of(
        "Troll",
        "Das Ticket wurde als Troll-Versuch eingestuft."
    ),
    TicketCloseReason.of(
        "Inaktivität",
        "Das Ticket wurde aufgrund von Inaktivität geschlossen."
    )
)

private val modalRegistry by lazy {
    getBean<ModalRegistry>()
}

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
        id = "whitelist",
        displayName = "Whitelist Ticket",
        description = "Erstelle ein Ticket, um auf dem Survival Server gewhitelisted zu werden.",
        emoji = "📜",
        viewPermission = DiscordPermission.TICKET_WHITELIST_VIEW,
        closeReasons = mutableObjectListOf(
            TicketCloseReason.of(
                displayName = "Anforderungen nicht erfüllt",
                description = "Du erfüllst nicht alle Anforderungen für eine Whitelist."
            )
        ).apply {
            addAll(defaultReasons)
        },
        modal = modalRegistry.get("ticket:whitelist").create()
    ),
    DISCORD_SUPPORT(
        id = "discord",
        displayName = "Discord Support Ticket",
        description = "Erstelle ein Ticket, um Support für den Discord Server zu erhalten.",
        emoji = "💬",
        viewPermission = DiscordPermission.TICKET_SUPPORT_DISCORD_VIEW,
        closeReasons = defaultReasons,
        modal = modalRegistry.get("ticket:support:discord").create()
    ),
    SURVIVAL_SUPPORT(
        id = "survival",
        displayName = "Survival Support Ticket",
        description = "Erstelle ein Ticket, um Support für den Survival Server zu erhalten.",
        emoji = "🛠️",
        viewPermission = DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
        closeReasons = defaultReasons,
        modal = modalRegistry.get("ticket:support:survival").create()
    ),
    EVENT_SUPPORT(
        id = "event",
        displayName = "Event Support Ticket",
        description = "Erstelle ein Ticket, um Support für Events zu erhalten.",
        emoji = "🎉",
        viewPermission = DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
        closeReasons = defaultReasons,
        modal = modalRegistry.get("ticket:support:event").create()
    ),
    REPORT(
        id = "report",
        displayName = "Report Ticket",
        description = "Erstelle ein Ticket, um einen Spieler zu melden.",
        emoji = "🚨",
        viewPermission = DiscordPermission.TICKET_REPORT_VIEW,
        closeReasons = mutableObjectListOf(
            TicketCloseReason.of(
                displayName = "Fall abgeschlossen",
                description = "Der gemeldete Fall wurde abgeschlossen."
            )
        ).apply {
            addAll(defaultReasons)
        },
        modal = modalRegistry.get("ticket:report").create()
    ),
    UNBAN(
        id = "unban",
        displayName = "Unban Antrag",
        description = "Erstelle ein Ticket, um einen Unban Antrag zu stellen.",
        emoji = "🔨",
        viewPermission = DiscordPermission.TICKET_UNBAN_VIEW,
        closeReasons = mutableObjectListOf(
            TicketCloseReason.of(
                displayName = "Antrag abgelehnt",
                description = "Dein Unban Antrag wurde abgelehnt."
            ),
            TicketCloseReason.of(
                displayName = "Ban aufgehoben",
                description = "Du wurdest entbannt. Bitte mache dich erneut mit unserem Regelwerk vertraut."
            ),
            TicketCloseReason.of(
                displayName = "Ban verkürzt",
                description = "Dein Ban wurde verkürzt."
            )
        ).apply {
            addAll(defaultReasons)
        },
        modal = modalRegistry.get("ticket:unban").create()
    ),
    BUGREPORT(
        id = "bugreport",
        displayName = "Bugreport Ticket",
        description = "Erstelle ein Ticket, um einen Bug zu melden.",
        emoji = "🐛",
        viewPermission = DiscordPermission.TICKET_BUG_VIEW,
        closeReasons = mutableObjectListOf(
            TicketCloseReason.of(
                displayName = "Bug bestätigt",
                description = "Der gemeldete Bug wurde bestätigt und an das Entwicklungsteam weitergeleitet."
            ),
            TicketCloseReason.of(
                displayName = "Bug nicht reproduzierbar",
                description = "Der Fehler konnte nicht reproduziert werden."
            ),
            TicketCloseReason.of(
                displayName = "Bug behoben",
                description = "Der gemeldete Fehler wurde behoben. Danke für deinen Bugreport!"
            )
        ).apply {
            addAll(defaultReasons)
        },
        modal = modalRegistry.get("ticket:bugreport").create()
    ),
    SERVER_SUPPORT(
        id = "server",
        displayName = "Server Support Ticket",
        description = "Erstelle ein Ticket, um Support für den Server zu erhalten.",
        emoji = "🖥️",
        viewPermission = DiscordPermission.TICKET_BUG_VIEW,
        closeReasons = defaultReasons,
        modal = modalRegistry.get("ticket:bugreport").create()
    ),
    APPLICATION(
        id = "application",
        displayName = "Bewerbung",
        description = "Erstelle ein Ticket, um eine Bewerbung zu starten.",
        emoji = "📝",
        viewPermission = DiscordPermission.UNKNOWN,
        closeReasons = mutableObjectListOf(
            TicketCloseReason.of(
                displayName = "Bewerbung abgelehnt",
                description = "Deine Bewerbung wurde abgelehnt."
            ),
            TicketCloseReason.of(
                displayName = "Bewerbung angenommen",
                description = "Deine Bewerbung wurde angenommen."
            )
        ).apply {
            addAll(defaultReasons)
        },
        modal = modalRegistry.get("ticket:application").create()
    )
}