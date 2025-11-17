package dev.slne.surf.discord.interaction.button.impl.feedback

import dev.slne.surf.discord.feedback.FeedbackService
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class FeedbackCreateButton(
    private val feedbackService: FeedbackService
) : DiscordButton {
    private val modalRegistry by lazy {
        getBean<ModalRegistry>()
    }

    override val id = "feedback:create"
    override val button = Button.success(id, translatable("feedback.button.create"))

    override suspend fun onClick(event: ButtonInteractionEvent) {
        event.replyModal(modalRegistry.get("modal:feedback:create").create()).queue()
    }
}