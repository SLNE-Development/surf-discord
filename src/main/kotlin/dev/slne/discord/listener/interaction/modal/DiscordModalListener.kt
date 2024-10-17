package dev.slne.discord.listener.interaction.modal

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.jda
import dev.slne.discord.message.MessageManager
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object DiscordModalListener {

    init {
        jda.listener<ModalInteractionEvent> { event ->
            val interaction = event.interaction
            val modalId = interaction.modalId
            val advancedModal = DiscordModalManager.getModal(modalId, event.user.id)

            if (advancedModal != null) {
                event.deferReply(true).await()
                advancedModal.handleUserSubmitModal(event)

                return@listener
            }

            event.replyEmbeds(
                MessageManager.getErrorEmbed(
                    "Fehler",
                    "Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen."
                )
            ).setEphemeral(true).await()
        }
    }
}
