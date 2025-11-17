package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.feedback.database.FeedbackRepository
import dev.slne.surf.discord.jda
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.springframework.stereotype.Component

@Component
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    suspend fun createFeedback(
        event: ModalInteractionEvent,
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
                        value = "${event.user.asTag} (${event.user.name})"
                        inline = true
                    }
                }
            )).submit(true).await()

        feedbackRepository.createFeedback(
            event.user,
            createdAt,
            post.threadChannel.idLong,
            category,
            title,
            content
        )

        event.reply("Dein Feedback wurde erfolgreich übermittelt. Vielen Dank! Ansehen: ${post.threadChannel.asMention}")
            .setEphemeral(true)
            .queue()
    }
}