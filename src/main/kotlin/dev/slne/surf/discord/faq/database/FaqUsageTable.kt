package dev.slne.surf.discord.faq.database

import dev.slne.surf.database.columns.time.offsetDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ReferenceOption
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import dev.slne.surf.discord.faq.FaqPlatform
import dev.slne.surf.discord.ticket.database.util.schemedName

object FaqUsageTable : Table(schemedName("faq_usage")) {
    val faqId = reference("faq_id", FaqTable.id, onDelete = ReferenceOption.CASCADE)
    val platform = enumerationByName<FaqPlatform>("source", 16)
    val usageCount = long("usage_count").default(0)
    val lastUsedAt = offsetDateTime("last_used_at").nullable()

    override val primaryKey = PrimaryKey(faqId, platform)
}
