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

enum class TicketType(
    val id: String,
    val displayName: String,
    val description: String,
    val available: Boolean,
    val emoji: String,
    val viewPermission: DiscordPermission,
    val closeReasons: ObjectList<TicketCloseReason>,
    val modal: Modal? = null
) {
    WHITELIST(
        "whitelist",
        "Whitelist Ticket",
        "Erstelle ein Ticket, um auf dem Survival Server gewhitelisted zu werden.",
        true,
        "📜",
        DiscordPermission.TICKET_WHITELIST_VIEW,
        mutableObjectListOf(
            TicketCloseReason.of(
                "Anforderungen nicht erfüllt",
                "Du erfüllst nicht alle Anforderungen für eine Whitelist."
            )
        ).apply {
            addAll(defaultReasons)
        },
        getBean<ModalRegistry>().get("ticket:whitelist").create()
    ),
    SUPPORT_DISCORD(
        "discord",
        "Discord Support Ticket",
        "Erstelle ein Ticket, um Support für den Discord Server zu erhalten.",
        true,
        "💬",
        DiscordPermission.TICKET_SUPPORT_DISCORD_VIEW,
        defaultReasons,
        getBean<ModalRegistry>().get("ticket:support:discord").create()
    ),
    SUPPORT_SURVIVAL(
        "survival",
        "Survival Support Ticket",
        "Erstelle ein Ticket, um Support für den Survival Server zu erhalten.",
        true,
        "🛠️",
        DiscordPermission.TICKET_SUPPORT_SURVIVAL_VIEW,
        defaultReasons,
        getBean<ModalRegistry>().get("ticket:support:survival").create()
    ),
    SUPPORT_EVENT(
        "event",
        "Event Support Ticket",
        "Erstelle ein Ticket, um Support für Events zu erhalten.",
        true,
        "🎉",
        DiscordPermission.TICKET_SUPPORT_EVENT_VIEW,
        defaultReasons,
        getBean<ModalRegistry>().get("ticket:support:event").create()
    ),
    REPORT(
        "report",
        "Report Ticket",
        "Erstelle ein Ticket, um einen Spieler zu melden.",
        true,
        "🚨",
        DiscordPermission.TICKET_REPORT_VIEW,
        mutableObjectListOf(
            TicketCloseReason.of(
                "Fall abgeschlossen",
                "Der gemeldete Fall wurde abgeschlossen."
            )
        ).apply {
            addAll(defaultReasons)
        },
        getBean<ModalRegistry>().get("ticket:report").create()
    ),
    UNBAN(
        "unban",
        "Unban Antrag",
        "Erstelle ein Ticket, um einen Unban Antrag zu stellen.",
        true,
        "🔨",
        DiscordPermission.TICKET_UNBAN_VIEW,
        mutableObjectListOf(
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
        ).apply {
            addAll(defaultReasons)
        },
        getBean<ModalRegistry>().get("ticket:unban").create()
    ),
    BUGREPORT(
        "bugreport",
        "Bugreport Ticket",
        "Erstelle ein Ticket, um einen Bug zu melden.",
        true,
        "🐛",
        DiscordPermission.TICKET_BUG_VIEW,
        mutableObjectListOf(
            TicketCloseReason.of(
                "Bug bestätigt",
                "Der gemeldete Bug wurde bestätigt und an das Entwicklungsteam weitergeleitet."
            ),
            TicketCloseReason.of(
                "Bug nicht reproduzierbar",
                "Der Fehler konnte nicht reproduziert werden."
            ),
            TicketCloseReason.of(
                "Bug behoben",
                "Der gemeldete Fehler wurde behoben. Danke für deinen Bugreport!"
            )
        ).apply {
            addAll(defaultReasons)
        },
        getBean<ModalRegistry>().get("ticket:bugreport").create()
    ),
    SURVIVAL_WHITELIST(
        "survival_whitelist",
        "Survival Whitelist Ticket",
        "Erstelle ein Ticket, um auf dem Survival Server gewhitelisted zu werden.",
        false,
        "🌍",
        DiscordPermission.TICKET_WHITELIST_VIEW,
        mutableObjectListOf(
            TicketCloseReason.of(
                "Anforderungen nicht erfüllt",
                "Du erfüllst nicht alle Anforderungen für eine Whitelist."
            )
        ).apply {
            addAll(defaultReasons)
        },
        getBean<ModalRegistry>().get("ticket:whitelist_survival").create()
    )
}