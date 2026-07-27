package dev.slne.surf.discord.interaction.modal

import dev.slne.surf.discord.interaction.modal.impl.ticket.ApplicationTicketModal

object ModalRegistry {
    private val modals = listOf(ApplicationTicketModal)

    private val modalMap by lazy { modals.associateBy { it.id } }

    fun getOrNull(id: String) = modalMap[id]
    fun get(id: String) = modalMap[id] ?: error("Modal with ID $id not found in registry!")
    fun all() = modalMap.values.toList()
}
