package dev.slne.surf.discord.interaction.modal

import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class ModalRegistry(modals: ObjectProvider<DiscordModal>) {
    private val modalMap by lazy { modals.associateBy { it.id } }

    fun getOrNull(id: String) = modalMap[id]
    fun get(id: String) = modalMap[id] ?: error("Modal with ID $id not found in registry!")
    fun all() = modalMap.values.toList()
}
