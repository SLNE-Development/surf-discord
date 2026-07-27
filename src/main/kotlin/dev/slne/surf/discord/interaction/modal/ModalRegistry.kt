package dev.slne.surf.discord.interaction.modal

import dev.slne.surf.discord.interaction.modal.impl.CustomCloseReasonModal
import dev.slne.surf.discord.interaction.modal.impl.DeadlineNotifyModal
import dev.slne.surf.discord.interaction.modal.impl.ticket.*

object ModalRegistry {
    private val modals = listOf(
        ApplicationTicketModal, CustomCloseReasonModal, DeadlineNotifyModal,
        BugreportTicketModal, ComplaintTicketModal, DiscordSupportTicketModal,
        EventSupportTicketModal, ShopPurchaseTicketModal, SurvivalSupportTicketModal,
        TwitchSupportTicketModal, UnbanTicketModal, ReportTicketModal
    )

    private val modalMap by lazy { modals.associateBy { it.id } }

    fun getOrNull(id: String) = modalMap[id]
    fun get(id: String) = modalMap[id] ?: error("Modal with ID $id not found in registry!")
    fun all() = modalMap.values.toList()
}
