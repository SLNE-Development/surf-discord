package dev.slne.surf.discord.faq.database

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ReferenceOption
import dev.slne.surf.database.table.AuditableLongIdTable
import dev.slne.surf.discord.faq.FaqLimits
import dev.slne.surf.discord.ticket.database.util.schemedName

object FaqTranslationTable : AuditableLongIdTable(schemedName("faq_translations")) {
    val faqId = reference("faq_id", FaqTable.id, onDelete = ReferenceOption.CASCADE)
    val locale = varchar("locale", 8)
    val question = varchar("question", FaqLimits.MAX_QUESTION_LENGTH)
    val longText = text("long_text")
    val shortText = varchar("short_text", FaqLimits.MAX_SHORT_TEXT_LENGTH)

    init {
        uniqueIndex(faqId, locale)
    }
}
