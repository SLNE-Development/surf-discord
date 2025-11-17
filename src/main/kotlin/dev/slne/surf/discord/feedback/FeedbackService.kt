package dev.slne.surf.discord.feedback

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.dsl.sendEmbed
import dev.slne.surf.discord.feedback.database.FeedbackRepository
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
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
                buttonRegistry.get("button:feedback:delete").button,
            )
        ).submit(true).await()

        post.threadChannel.addThreadMember(event.user).queue()

        feedbackRepository.createFeedback(
            event.user,
            createdAt,
            post.threadChannel.idLong,
            category,
            title,
            content
        )

        event.reply(translatable("feedback.submitted", post.threadChannel.asMention))
            .setEphemeral(true)
            .queue()
    }

    suspend fun approveFeedback(
        event: ButtonInteractionEvent,
        thread: ThreadChannel,
        approvedBy: User
    ) {
        thread.sendEmbed {
            title = translatable("feedback.embed.approved.title")
            description = translatable("feedback.embed.approved.description")
            color = Colors.INFO
            footer = translatable("feedback.embed.approved.footer", approvedBy.name)
        }.submit(true).await()

        event.reply(translatable("feedback.approved")).setEphemeral(true).queue()

        thread.manager.setLocked(true).queue()
        thread.manager.setArchived(true).queue()

        feedbackRepository.approveFeedback(thread.idLong, approvedBy)
    }

    suspend fun declineFeedback(
        event: ButtonInteractionEvent,
        thread: ThreadChannel,
        declinedBy: User
    ) {
        thread.sendEmbed {
            title = translatable("feedback.embed.declined.title")
            description = translatable("feedback.embed.declined.description")
            color = Colors.ERROR
            footer = translatable("feedback.embed.declined.footer", declinedBy.name)
        }.submit(true).await()

        event.reply(translatable("feedback.declined")).setEphemeral(true).queue()

        thread.manager.setLocked(true).queue()
        thread.manager.setArchived(true).queue()

        feedbackRepository.declineFeedback(thread.idLong)
    }

    suspend fun deleteFeedback(
        thread: ThreadChannel
    ) {
        feedbackRepository.deleteFeedback(thread.idLong)
        thread.delete().queue()
    }
}