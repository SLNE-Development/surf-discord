package dev.slne.surf.discord.config.feedback

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackCategoryConfig(
    val name: String,
    val displayName: String,
    val postTagId: Long?
)