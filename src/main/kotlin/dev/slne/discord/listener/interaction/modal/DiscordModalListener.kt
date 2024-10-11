package dev.slne.discord.listener.interaction.modal

import dev.slne.discord.discord.interaction.modal.DiscordModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.message.MessageManager.Companion.getErrorEmbed
import jakarta.annotation.Nonnull
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.modals.ModalInteraction
import java.util.concurrent.CompletableFuture

/**
 * The type Discord modal listener.
 */
@DiscordListener
class DiscordModalListener : ListenerAdapter() {
    override fun onModalInteraction(@Nonnull event: ModalInteractionEvent) {
        val interaction: ModalInteraction = event.getInteraction()
        val modalId: String = interaction.getModalId()
        val modal: DiscordModal? = DiscordModalManager.Companion.INSTANCE.getModal(modalId)

        if (modal != null) {
            modal.execute(event)
            return
        }

        val advancedModal: DiscordStepChannelCreationModal? =
            DiscordModalManager.Companion.INSTANCE.getAdvancedModal(
                modalId, event.getUser().getId()
            )

        if (advancedModal != null) {
            event.deferReply(true).queue()
            CompletableFuture.runAsync(Runnable { advancedModal.handleUserSubmitModal(event) })
            return
        }

        event.replyEmbeds(
            getErrorEmbed(
                "Fehler",
                "Deine Aktion konnte nicht durchgeführt werden oder ist abgelaufen."
            )
        ).setEphemeral(true).queue()
    }
}
