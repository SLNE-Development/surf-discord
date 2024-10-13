package dev.slne.discord.discord.interaction.modal

import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object DiscordModalManager {

    private val advancedModals =
        Object2ObjectOpenHashMap<String, () -> DiscordStepChannelCreationModal>()
    private val currentUserModals =
        Object2ObjectOpenHashMap<String, DiscordStepChannelCreationModal>()

    fun registerAdvancedModal(
        modalId: String,
        creator: () -> DiscordStepChannelCreationModal
    ) = advancedModals.putIfAbsent(modalId, creator)

    fun getAdvancedModal(customId: String, userId: String): DiscordStepChannelCreationModal? {
        val modalCreator = advancedModals[customId] ?: return null
        return currentUserModals[userId] ?: modalCreator()
    }

    fun setCurrentUserModal(userId: String, modal: DiscordStepChannelCreationModal) {
        currentUserModals[userId] = modal
    }
}
