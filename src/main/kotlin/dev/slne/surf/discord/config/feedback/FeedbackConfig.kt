package dev.slne.surf.discord.config.feedback

import dev.slne.surf.discord.feedback.FeedbackCategory
import dev.slne.surf.discord.util.formattedEnumEntryName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackConfig(
    val feedbackChannel: Long,
    val feedbackLogChannel: Long,
    val feedbackApprovedTag: Long,
    val feedbackDeclinedTag: Long,
    val categories: List<FeedbackCategoryConfig> = FeedbackCategory.entries.map {
        FeedbackCategoryConfig(
            name = it.name,
            displayName = it.name.formattedEnumEntryName,
            postTagId = null
        )
    }
)