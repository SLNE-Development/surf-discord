package dev.slne.surf.discord.announcement

data class DiscordAnnouncement(
    val authorName: String,
    val authorId: Long,
    val messageId: Long,
    val channelId: Long,

    val title: String,
    val content: String
)