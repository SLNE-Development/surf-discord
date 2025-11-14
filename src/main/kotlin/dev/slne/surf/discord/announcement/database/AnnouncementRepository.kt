package dev.slne.surf.discord.announcement.database

import dev.slne.surf.discord.announcement.DiscordAnnouncement
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class AnnouncementRepository {
    suspend fun createAnnouncement(
        authorName: String,
        authorId: Long,
        messageId: Long,
        channelId: Long,
        title: String,
        content: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        AnnouncementTable.insert {
            it[this.authorName] = authorName
            it[this.authorId] = authorId
            it[this.messageId] = messageId
            it[this.channelId] = channelId
            it[this.title] = title
            it[this.content] = content
        }
    }

    suspend fun editAnnouncement(
        messageId: Long,
        title: String,
        newContent: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        AnnouncementTable.update({ AnnouncementTable.messageId eq messageId }) {
            it[this.title] = title
            it[this.content] = newContent
        }
    }

    suspend fun getAnnouncement(messageId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        AnnouncementTable.selectAll().where(AnnouncementTable.messageId eq messageId)
            .firstNotNullOfOrNull { row ->
                DiscordAnnouncement(
                    row[AnnouncementTable.authorName],
                    row[AnnouncementTable.authorId],
                    row[AnnouncementTable.messageId],
                    row[AnnouncementTable.channelId],
                    row[AnnouncementTable.title],
                    row[AnnouncementTable.content]
                )
            }
    }

    suspend fun deleteAnnouncement(messageId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        AnnouncementTable.deleteWhere { AnnouncementTable.messageId eq messageId }
    }
}