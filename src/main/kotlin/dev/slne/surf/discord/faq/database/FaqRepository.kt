package dev.slne.surf.discord.faq.database

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.inList
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.plus
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.discord.faq.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList

object FaqRepository {
    suspend fun findAll(): List<FaqEntry> = suspendTransaction {
        val translations = FaqTranslationTable.selectAll()
            .toList()
            .groupBy(
                { it[FaqTranslationTable.faqId].value },
                { it.toLocaleAndTranslation() }
            )

        FaqTable.selectAll()
            .toList()
            .map { row ->
                val id = row[FaqTable.id].value

                FaqEntry(
                    id = id,
                    key = row[FaqTable.key],
                    activeDiscord = row[FaqTable.activeDiscord],
                    activeMinecraft = row[FaqTable.activeMinecraft],
                    activePanel = row[FaqTable.activePanel],
                    translations = translations[id].orEmpty()
                        .mapNotNull { (locale, translation) -> locale?.let { it to translation } }
                        .toMap()
                )
            }
            .filter { FaqLocale.DEFAULT in it.translations }
    }

    suspend fun isEmpty(): Boolean = suspendTransaction {
        FaqTable.select(FaqTable.id).limit(1).singleOrNull() == null
    }

    suspend fun create(key: String, translations: Map<FaqLocale, FaqTranslation>) = suspendTransaction {
        val faqId = FaqTable.insertAndGetId { it[FaqTable.key] = key }

        translations.forEach { (locale, translation) ->
            FaqTranslationTable.insert {
                it[FaqTranslationTable.faqId] = faqId
                it[FaqTranslationTable.locale] = locale.code
                it[FaqTranslationTable.question] = translation.question
                it[FaqTranslationTable.longText] = translation.longText
                it[FaqTranslationTable.shortText] = translation.shortText
            }
        }
    }

    suspend fun addUsage(deltas: Map<FaqUsageKey, FaqUsageDelta>) = suspendTransaction {
        val existingIds = FaqTable.select(FaqTable.id)
            .where { FaqTable.id inList deltas.keys.map { it.faqId }.distinct() }
            .toList()
            .map { it[FaqTable.id].value }
            .toSet()

        deltas.filterKeys { it.faqId in existingIds }.forEach { (key, delta) ->
            FaqUsageTable.upsert(
                FaqUsageTable.faqId,
                FaqUsageTable.platform,
                onUpdate = {
                    it[FaqUsageTable.usageCount] = FaqUsageTable.usageCount + delta.count
                    it[FaqUsageTable.lastUsedAt] = delta.lastUsedAt
                }
            ) {
                it[FaqUsageTable.faqId] = key.faqId
                it[FaqUsageTable.platform] = key.platform
                it[FaqUsageTable.usageCount] = delta.count
                it[FaqUsageTable.lastUsedAt] = delta.lastUsedAt
            }
        }
    }

    suspend fun addSends(sends: List<FaqSend>) = suspendTransaction {
        val existingIds = FaqTable.select(FaqTable.id)
            .where { FaqTable.id inList sends.map { it.faqId }.distinct() }
            .toList()
            .map { it[FaqTable.id].value }
            .toSet()

        sends.filter { it.faqId in existingIds }.forEach { send ->
            FaqSendTable.insert {
                it[FaqSendTable.faqId] = send.faqId
                it[FaqSendTable.platform] = send.platform
                it[FaqSendTable.senderId] = send.sender.id.take(FaqSendTable.MAX_SENDER_ID_LENGTH)
                it[FaqSendTable.senderName] = send.sender.name.take(FaqSendTable.MAX_SENDER_NAME_LENGTH)
                it[FaqSendTable.sentAt] = send.sentAt
            }
        }
    }

    private fun ResultRow.toLocaleAndTranslation() =
        FaqLocale.fromCode(this[FaqTranslationTable.locale]) to FaqTranslation(
            question = this[FaqTranslationTable.question],
            longText = this[FaqTranslationTable.longText],
            shortText = this[FaqTranslationTable.shortText]
        )
}
