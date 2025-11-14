package dev.slne.surf.discord.interaction.modal.impl.announcement

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Component

@Component
class EditAnnouncementModal(
    private val announcementService: AnnouncementService
) : DiscordModal {
    override val id = "announcement:edit"

    override suspend fun create(hook: InteractionHook, vararg data: String) =
        modal(id, translatable("announcement.modal.edit.title")) {
            textInput {
                id = "announcement-title"
                label = translatable("announcement.modal.edit.field.title.label")
                value = data[1]
                required = true
                style = TextInputStyle.SHORT
            }

            textInput {
                id = "announcement-content"
                label = translatable("announcement.modal.edit.field.content.label")
                value = data[2]
                required = true
                style = TextInputStyle.PARAGRAPH
            }

            textInput {
                id = "announcement-message-id"
                label = translatable("announcement.modal.edit.field.id.label")
                value = data[0]
                required = true
                style = TextInputStyle.SHORT
            }
        }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction

        val title = interaction.getValue("announcement-title")?.asString ?: return
        val content = interaction.getValue("announcement-content")?.asString ?: return
        val messageId =
            interaction.getValue("announcement-message-id")?.asString?.toLong() ?: return

        val announcement = announcementService.getAnnouncement(messageId)

        if (announcement == null) {
            event.reply(translatable("announcement.not-found")).setEphemeral(true).queue()
            return
        }

        announcementService.editAnnouncement(announcement, title, content)

        event.reply(translatable("announcement.edited")).setEphemeral(true).queue()
    }
}