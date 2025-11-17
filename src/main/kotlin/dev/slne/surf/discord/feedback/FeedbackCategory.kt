package dev.slne.surf.discord.feedback

enum class FeedbackCategory(val numerationId: Int) {
    SURVIVAL_SERVER(0),
    EVENT_SERVER(1),
    OTHER_SERVER(2),
    DISCORD_SERVER(3),
    TWITCH(4),
    YOUTUBE(5),
    OTHER(6)
}