package dev.slne.discord.discord.interaction.modal

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.bugreport.BugReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.discord.DiscordSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.event.EventSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.support.survival.SurvivalSupportTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.WhitelistTicketChannelCreationModal
import dev.slne.discord.ticket.TicketType

object ChannelCreationModalManager {

    init {
        register(
            WhitelistTicketChannelCreationModal.MODAL_ID,
            ::WhitelistTicketChannelCreationModal
        )
        register(UnbanTicketChannelCreationModal.MODAL_ID, ::UnbanTicketChannelCreationModal)
        register(ReportTicketChannelCreationModal.MODAL_ID, ::ReportTicketChannelCreationModal)
        register(
            BugReportTicketChannelCreationModal.MODAL_ID,
            ::BugReportTicketChannelCreationModal
        )
        register(
            SurvivalSupportTicketChannelCreationModal.MODAL_ID,
            ::SurvivalSupportTicketChannelCreationModal
        )
        register(
            EventSupportTicketChannelCreationModal.MODAL_ID,
            ::EventSupportTicketChannelCreationModal
        )
        register(
            DiscordSupportTicketChannelCreationModal.MODAL_ID,
            ::DiscordSupportTicketChannelCreationModal
        )
    }

    fun register(modalId: String, supplier: () -> DiscordStepChannelCreationModal) =
        DiscordModalManager.registerAdvancedModal(modalId, supplier)

    fun getModalId(annotation: ChannelCreationModal) =
        annotation.modalId.ifEmpty { annotation.ticketType.name }

    fun getTicketType(annotation: ChannelCreationModal): TicketType = annotation.ticketType
}
