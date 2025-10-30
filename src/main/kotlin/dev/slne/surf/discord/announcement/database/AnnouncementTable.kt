package dev.slne.surf.discord.announcement.database

import org.jetbrains.exposed.dao.id.LongIdTable

object AnnouncementTable : LongIdTable("discord_announcements") {
    val authorName = varchar("author_name", 100)
    val authorId = long("author_id")
    val messageId = long("message_id").uniqueIndex()
    val title = varchar("title", 200)
    val content = largeText("content")
}