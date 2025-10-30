package dev.slne.surf.discord.interaction.modal.impl.announcement

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class EditAnnouncementModal(
    private val announcementService: AnnouncementService
) : DiscordModal {
    override val id = "announcement:edit"

    override suspend fun create(hook: InteractionHook, vararg data: String) =
        modal(id, "Ankündigung bearbeiten") {
            field {
                id = "announcement-title"
                label = "Titel der Ankündigung"
                value = data[1]
                required = true
                style = TextInputStyle.SHORT
            }

            field {
                id = "announcement-content"
                label = "Inhalt der Ankündigung"
                value = data[2]
                required = true
                style = TextInputStyle.PARAGRAPH
            }

            field {
                id = "announcement-message-id"
                label = "Message ID"
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
            event.reply("Die Ankündigung wurde nicht gefunden.").setEphemeral(true).queue()
            return
        }

        announcementService.editAnnouncement(announcement, title, content)

        event.reply("Die Ankündigung wurde erfolgreich bearbeitet.").setEphemeral(true).queue()
    }
}