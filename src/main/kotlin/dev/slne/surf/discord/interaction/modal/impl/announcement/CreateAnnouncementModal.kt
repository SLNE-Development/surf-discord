package dev.slne.surf.discord.interaction.modal.impl.announcement

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class CreateAnnouncementModal(
    private val announcementService: AnnouncementService
) : DiscordModal {
    override val id = "announcement:create"

    override fun create() = modal(id, translatable("announcement.modal.create.title")) {
        field {
            id = "title"
            label = translatable("announcement.modal.create.field.title.label")
            required = true
            placeholder = translatable("announcement.modal.create.field.title.placeholder")
            style = TextInputStyle.SHORT
        }
        field {
            id = "content"
            label = translatable("announcement.modal.create.field.content.label")
            style = TextInputStyle.PARAGRAPH
            required = true
            placeholder = translatable("announcement.modal.create.field.content.placeholder")
            lengthRange = 10..4000
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction

        val title = interaction.getValue("title")?.asString ?: return
        val content = interaction.getValue("content")?.asString ?: return

        announcementService.sendAnnouncement(event.user, title, content, interaction.messageChannel)
        
        event.reply(translatable("announcement.created")).setEphemeral(true)
            .queue()
    }
}