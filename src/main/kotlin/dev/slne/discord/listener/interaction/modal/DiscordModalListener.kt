package dev.slne.discord.listener.interaction.modal

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.message.MessageManager
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

object DiscordModalListener {

    init {
        DiscordBot.jda.listener<ModalInteractionEvent> { event ->
            val interaction = event.interaction
            val modalId = interaction.modalId
            val modal = DiscordModalManager.getModal(modalId)

            if (modal != null) {
                modal.execute(event)

                return@listener
            }

            val advancedModal = DiscordModalManager.getAdvancedModal(modalId, event.user.id)

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
