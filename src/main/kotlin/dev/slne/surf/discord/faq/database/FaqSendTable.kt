package dev.slne.surf.discord.faq.database

import dev.slne.surf.database.columns.time.offsetDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ReferenceOption
import dev.slne.surf.database.table.AuditableLongIdTable
import dev.slne.surf.discord.faq.FaqPlatform
import dev.slne.surf.discord.ticket.database.util.schemedName

object FaqSendTable : AuditableLongIdTable(schemedName("faq_sends")) {
    const val MAX_SENDER_ID_LENGTH = 64
    const val MAX_SENDER_NAME_LENGTH = 64

    val faqId = reference("faq_id", FaqTable.id, onDelete = ReferenceOption.CASCADE)
    val platform = enumerationByName<FaqPlatform>("source", 16)
    val senderId = varchar("sender_id", MAX_SENDER_ID_LENGTH)
    val senderName = varchar("sender_name", MAX_SENDER_NAME_LENGTH)
    val sentAt = offsetDateTime("sent_at")

    init {
        index(false, faqId)
        index(false, senderId)
    }
}
