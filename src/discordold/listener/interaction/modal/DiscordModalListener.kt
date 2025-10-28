package dev.slne.discordold.listener.interaction.modal

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.message.MessageManager
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import org.springframework.stereotype.Component

@Component
class DiscordModalListener(
    private val jda: JDA,
    private val discordModalManager: DiscordModalManager,
    private val messageManager: MessageManager
) {

    @PostConstruct
    fun registerListener() {
        jda.listener<ModalInteractionEvent> { event ->
            val interaction = event.interaction
            val modalId = interaction.modalId
            val advancedModal = discordModalManager.getModal(modalId, event.user.id)

            if (advancedModal != null) {
                event.deferReply(true).await()
                advancedModal.handleUserSubmitModal(event)

                return@listener
            }

            event.replyEmbeds(
                messageManager.getErrorEmbed(
                    "Fehler",
                    "Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen."
                )
            ).setEphemeral(true).await()
        }
    }
}
