package dev.slne.surf.discord.interaction.button.impl.feedback

import dev.slne.surf.discord.feedback.FeedbackService
import dev.slne.surf.discord.interaction.button.DiscordButton
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class FeedbackApproveButton(
    private val feedbackService: FeedbackService
) : DiscordButton {
    override val id = "button:feedback:approve"
    override val button = Button.of(
        ButtonStyle.SECONDARY,
        id,
        "Annehmen",
        Emoji.fromCustom("checkmark", 1433072075446423754, false)
    )

    override suspend fun onClick(event: ButtonInteractionEvent) {
        feedbackService.approveFeedback(event.channel.asThreadChannel(), event.user)

        event.reply("Feedback angenommen.").setEphemeral(true).queue()
    }
}