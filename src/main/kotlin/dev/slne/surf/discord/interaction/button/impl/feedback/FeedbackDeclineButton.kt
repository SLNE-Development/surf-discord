package dev.slne.surf.discord.interaction.button.impl.feedback

import dev.slne.surf.discord.feedback.FeedbackService
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class FeedbackDeclineButton(
    private val feedbackService: FeedbackService
) : DiscordButton {
    override val id = "button:feedback:decline"
    override val button = Button.of(
        ButtonStyle.SECONDARY,
        id,
        translatable("feedback.button.decline"),
        Emoji.fromCustom("cross", 1433072192274305135, false)
    )

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.FEEDBACK_DECLINE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        feedbackService.declineFeedback(event, event.channel.asThreadChannel(), event.user)
    }
}