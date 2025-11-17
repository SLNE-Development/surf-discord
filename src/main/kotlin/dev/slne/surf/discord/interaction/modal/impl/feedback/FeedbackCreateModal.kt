package dev.slne.surf.discord.interaction.modal.impl.feedback

import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.feedback.FeedbackCategory
import dev.slne.surf.discord.feedback.FeedbackService
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class FeedbackCreateModal(
    private val feedbackService: FeedbackService
) : DiscordModal {
    override val id = "modal:feedback:create"

    private val selectMenuRegistry by lazy {
        getBean<SelectMenuRegistry>()
    }

    override fun create() = modal(id, "Feedback erstellen") {
        selectMenu(
            "modal:feedback:create:type",
            selectMenuRegistry.get("select:feedback:category").create()
        )

        textInput {
            id = "modal:feedback:create:title"
            label = "Titel"
            style = TextInputStyle.SHORT
            required = true
            lengthRange = 5..200
        }

        textInput {
            id = "modal:feedback:create:content"
            label = "Dein Feedback"
            style = TextInputStyle.PARAGRAPH
            required = true
            lengthRange = 20..4000
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val category =
            FeedbackCategory.entries.firstOrNull { it.name == event.getValue("modal:feedback:create:type")?.asString }
                ?: return
        val content = event.getValue("modal:feedback:create:content")?.asString ?: return
        val title = event.getValue("modal:feedback:create:title")?.asString ?: return

        feedbackService.createFeedback(event, category, title, content)
    }
}