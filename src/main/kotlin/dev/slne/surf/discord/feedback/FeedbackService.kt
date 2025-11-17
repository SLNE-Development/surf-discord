package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.sendEmbed
import dev.slne.surf.discord.feedback.database.FeedbackRepository
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.springframework.stereotype.Component

@Component
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    private val buttonRegistry by lazy {
        getBean<ButtonRegistry>()
    }

    suspend fun createFeedback(
        event: ModalInteractionEvent,
        category: FeedbackCategory,
        title: String,
        content: String
    ) {
        val createdAt = System.currentTimeMillis()
        val channel = jda.getForumChannelById(botConfig.feedback.feedbackChannel)
            ?: error("Feedback channel not found")

        val post = channel.createForumPost(
            title, MessageCreateData.fromContent(
                "$title\n\n$content"
            )
        ).setTags(
            buildList {
                botConfig.feedback.categories.firstOrNull { it.name == category.name }?.postTagId?.let {
                    add(ForumTagSnowflake.fromId(it))
                }
            }
        ).addComponents(
            ActionRow.of(
                buttonRegistry.get("button:feedback:approve").button,
                buttonRegistry.get("button:feedback:decline").button,
            )
        ).submit(true).await()

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

    suspend fun approveFeedback(thread: ThreadChannel, approvedBy: User) {
        thread.sendEmbed {
            title = "Feedback genehmigt"
            description =
                "Dein eingereichtes Feedback wurde genehmigt und wird nun weiter intern verarbeitet. Vielen Dank für deine Unterstützung!"
            color = Colors.INFO
            footer = "Genehmigt von ${approvedBy.name}"
        }.submit(true).await()

        thread.manager.setArchived(true).queue()
        thread.manager.setLocked(true).queue()

        feedbackRepository.approveFeedback(thread.idLong, approvedBy)
    }

    suspend fun declineFeedback(thread: ThreadChannel, declinedBy: User) {
        thread.sendEmbed {
            title = "Feedback abgelehnt"
            description =
                "Dein eingereichtes Feedback wurde leider abgelehnt. Wir danken dir dennoch für deine Mühe und dein Engagement!"
            color = Colors.ERROR
            footer = "Abgelehnt von ${declinedBy.name}"
        }.submit(true).await()

        thread.appliedTags

        thread.manager.setArchived(true).queue()
        thread.manager.setLocked(true).queue()

        feedbackRepository.declineFeedback(thread.idLong)
    }
}