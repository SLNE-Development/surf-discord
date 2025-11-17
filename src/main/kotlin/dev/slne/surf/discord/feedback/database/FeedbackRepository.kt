package dev.slne.surf.discord.feedback.database

import dev.slne.surf.discord.feedback.FeedbackCategory
import kotlinx.coroutines.Dispatchers
import net.dv8tion.jda.api.entities.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository

@Repository
class FeedbackRepository {
    suspend fun createFeedback(
        author: User,
        createdAt: Long,
        postId: Long,
        category: FeedbackCategory,
        title: String,
        content: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        FeedbackTable.insert {
            it[authorId] = author.idLong
            it[authorName] = author.name
            it[authorAvatarUrl] = author.avatarUrl

            it[this.createdAt] = createdAt
            it[this.updatedAt] = createdAt

            it[this.postThreadId] = postId
            it[this.category] = category
            it[this.title] = title
            it[this.content] = content
        }
    }

    suspend fun approveFeedback(threadId: Long, acceptedBy: User) =
        newSuspendedTransaction(Dispatchers.IO) {
            FeedbackTable.update(where = { FeedbackTable.postThreadId eq threadId }) {
                it[accepted] = true
                it[acceptedById] = acceptedBy.idLong
                it[acceptedByName] = acceptedBy.name
                it[acceptedByAvatarUrl] = acceptedBy.avatarUrl
                it[acceptedAt] = System.currentTimeMillis()
                it[updatedAt] = System.currentTimeMillis()
            }
        }

    suspend fun declineFeedback(threadId: Long) = newSuspendedTransaction(Dispatchers.IO) {
        FeedbackTable.update(where = { FeedbackTable.postThreadId eq threadId }) {
            it[accepted] = true
            it[acceptedAt] = System.currentTimeMillis()
            it[updatedAt] = System.currentTimeMillis()
        }
    }
}