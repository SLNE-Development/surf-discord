package dev.slne.discord.discord.interaction.modal

import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.bugreport.BugReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.discord.DiscordSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.event.EventSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.survival.SurvivalSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.WhitelistTicketChannelCreationModal
import dev.slne.discord.ticket.TicketType
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object DiscordModalManager {

    init {
        register(::WhitelistTicketChannelCreationModal)
        register(::UnbanTicketChannelCreationModal)
        register(::ReportTicketChannelCreationModal)
        register(::BugReportTicketChannelCreationModal)
        register(::SurvivalSupportTicketChannelCreationModal)
        register(::EventSupportTicketChannelCreationModal)
        register(::DiscordSupportTicketChannelCreationModal)
    }

    private val modals =
        Object2ObjectOpenHashMap<String, () -> DiscordStepChannelCreationModal>()
    private val currentUserModals =
        Object2ObjectOpenHashMap<String, DiscordStepChannelCreationModal>()
    private val byTicketType =
        Object2ObjectOpenHashMap<TicketType, () -> DiscordStepChannelCreationModal>()

    private fun register(supplier: () -> DiscordStepChannelCreationModal) {
        val temp = supplier()

        check(temp.id !in modals) { "Modal with id ${temp.id} is already registered" }

        modals[temp.id] = supplier
        byTicketType[temp.ticketType] = supplier
    }

    fun getModal(customId: String, userId: String): DiscordStepChannelCreationModal? {
        val modalCreator = modals[customId] ?: return null
        return currentUserModals[userId] ?: modalCreator()
    }

    fun createByTicketType(ticketType: TicketType): DiscordStepChannelCreationModal {
        return byTicketType[ticketType]?.invoke() ?: error("No modal for ticket type $ticketType")
    }

    fun setCurrentUserModal(userId: String, modal: DiscordStepChannelCreationModal) {
        currentUserModals[userId] = modal
    }
}
