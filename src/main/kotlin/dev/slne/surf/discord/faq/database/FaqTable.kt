package dev.slne.surf.discord.faq.database

import dev.slne.surf.database.table.AuditableLongIdTable
import dev.slne.surf.discord.faq.FaqLimits
import dev.slne.surf.discord.ticket.database.util.schemedName

object FaqTable : AuditableLongIdTable(schemedName("faq_entries")) {
    val key = varchar("key", FaqLimits.MAX_KEY_LENGTH).uniqueIndex()
    val activeDiscord = bool("active_discord").default(true)
    val activeMinecraft = bool("active_minecraft").default(true)
    val activePanel = bool("active_panel").default(true)
}
