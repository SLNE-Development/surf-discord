package dev.slne.surf.discord.interaction.button.impl.feedback

import dev.slne.surf.discord.feedback.FeedbackService
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class FeedbackDeleteButton(
    private val feedbackService: FeedbackService
) : DiscordButton {
    override val id = "button:feedback:delete"
    override val button = Button.danger(id, "Löschen")

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.FEEDBACK_DELETE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        event.reply("Das Feedback wird gelöscht...").setEphemeral(true).queue()
        feedbackService.deleteFeedback(event.channel.asThreadChannel())
    }
}