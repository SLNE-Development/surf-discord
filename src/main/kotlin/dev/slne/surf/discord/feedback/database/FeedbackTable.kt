package dev.slne.surf.discord.feedback.database

import dev.slne.surf.discord.feedback.FeedbackCategory
import org.jetbrains.exposed.dao.id.LongIdTable

object FeedbackTable : LongIdTable("discord_feedback") {
    val authorId = long("author_id")
    val authorName = varchar("author_name", 100)
    val authorAvatarUrl = varchar("author_avatar_url", 200).nullable()

    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    val accepted = bool("accepted").nullable()
    val acceptedById = long("accepted_by_id").nullable()
    val acceptedByName = varchar("accepted_by_name", 100).nullable()
    val acceptedByAvatarUrl = varchar("accepted_by_avatar_url", 200).nullable()
    val acceptedAt = long("accepted_at").nullable()

    val postThreadId = long("post_thread_id")
    val category = enumeration<FeedbackCategory>("category")
    val title = varchar("title", 250)
    val content = largeText("content")
}