package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.feedback.database.FeedbackRepository
import dev.slne.surf.discord.jda
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.springframework.stereotype.Component

@Component
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    suspend fun createFeedback(
        author: User,
        category: FeedbackCategory,
        title: String,
        content: String
    ) {
        val createdAt = System.currentTimeMillis()
        val channel = jda.getForumChannelById(botConfig.channels.feedbackChannel)
            ?: error("Feedback channel not found")

        val post = channel.createForumPost(
            title, MessageCreateData.fromEmbeds(
                embed {
                    this.title = title
                    this.description = content

                    field {
                        name = "Kategorie"
                        value = category.name.lowercase().replaceFirstChar { it.uppercase() }
                        inline = true
                    }

                    field {
                        name = "Erstellt von"
                        value = "${author.asTag} (${author.name})"
                        inline = true
                    }
                }
            )).submit(true).await()

        feedbackRepository.createFeedback()
    }
}