package dev.slne.surf.discord.interaction.modal.impl.announcement

import dev.slne.surf.discord.announcement.AnnouncementService
import dev.slne.surf.discord.dsl.modal
import dev.slne.surf.discord.interaction.modal.DiscordModal
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import org.springframework.stereotype.Component

@Component
class CreateAnnouncementModal(
    private val announcementService: AnnouncementService
) : DiscordModal {
    override val id = "announcement:create"

    override fun create() = modal(id, "Ankündigung erstellen") {
        field {
            id = "title"
            label = "Titel der Ankündigung"
            required = true
            placeholder = "Gib den Titel der Ankündigung ein"
            style = TextInputStyle.SHORT
        }
        field {
            id = "content"
            label = "Inhalt der Ankündigung"
            style = TextInputStyle.PARAGRAPH
            required = true
            placeholder = "Gib den Inhalt der Ankündigung ein"
            lengthRange = 10..4000
        }
    }

    override suspend fun onSubmit(event: ModalInteractionEvent) {
        val interaction = event.interaction

        val title = interaction.getValue("title")?.asString ?: return
        val content = interaction.getValue("content")?.asString ?: return

        announcementService.sendAnnouncement(event.user, title, content, interaction.messageChannel)
        event.reply("Die Ankündigung wurde erfolgreich erstellt und versendet.").setEphemeral(true)
            .queue()
    }
}