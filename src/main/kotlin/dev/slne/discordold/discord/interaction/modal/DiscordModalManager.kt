package dev.slne.discordold.discord.interaction.modal

import dev.slne.discordold.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.bugreport.BugReportTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.report.ReportTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.support.discord.DiscordSupportTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.support.event.EventSupportTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.support.survival.SurvivalSupportTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.unban.UnbanTicketChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.creator.whitelist.WhitelistTicketChannelCreationModal
import dev.slne.discordold.message.MessageManager
import dev.slne.discordold.persistence.service.punishment.PunishmentNoteService
import dev.slne.discordold.persistence.service.punishment.PunishmentService
import dev.slne.discordold.persistence.service.ticket.TicketService
import dev.slne.discordold.persistence.service.user.UserService
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import dev.slne.discordold.ticket.TicketChannelHelper
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.TicketType
import dev.slne.discordold.util.mutableObject2ObjectMapOf
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class DiscordModalManager(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    private val ticketService: TicketService,
    private val ticketCreator: TicketCreator,
    private val ticketChannelHelper: TicketChannelHelper,
    private val punishmentService: PunishmentService,
    private val messageManager: MessageManager,
    private val punishmentNoteService: PunishmentNoteService,
) {

    // @formatter:off
    private val modals = mutableObject2ObjectMapOf<String, () -> DiscordStepChannelCreationModal>()
    private val currentUserModals = mutableObject2ObjectMapOf<String, DiscordStepChannelCreationModal>()
    private val byTicketType = mutableObject2ObjectMapOf<TicketType, () -> DiscordStepChannelCreationModal>()
    // @formatter:on

    @PostConstruct
    fun init() {
        // @formatter:off
        register { WhitelistTicketChannelCreationModal(whitelistService, userService, ticketCreator, ticketChannelHelper, this) }
        register { UnbanTicketChannelCreationModal(punishmentService, ticketCreator, ticketChannelHelper, this,punishmentNoteService, ticketService) }
        register { ReportTicketChannelCreationModal(ticketCreator, ticketChannelHelper, this, userService, whitelistService, messageManager) }
        register { BugReportTicketChannelCreationModal(ticketCreator, ticketChannelHelper, this) }
        register { SurvivalSupportTicketChannelCreationModal(whitelistService, ticketCreator, ticketChannelHelper, this) }
        register { EventSupportTicketChannelCreationModal(ticketCreator, ticketChannelHelper, this) }
        register { DiscordSupportTicketChannelCreationModal(ticketCreator, ticketChannelHelper, this) }
        // @formatter:on
    }

    private fun register(supplier: () -> DiscordStepChannelCreationModal) {
        val temp = supplier()

        check(temp.id !in modals) { "Modal with id ${temp.id} is already registered" }

        modals[temp.id] = supplier
        byTicketType[temp.ticketType] = supplier
    }

    fun getModal(customId: String, userId: String): DiscordStepChannelCreationModal? {
        val modalCreator = modals[customId] ?: return null
        return currentUserModals[userId] ?: modalCreator.invoke()
    }

    fun createByTicketType(ticketType: TicketType): DiscordStepChannelCreationModal {
        return byTicketType[ticketType]?.invoke() ?: error("No modal for ticket type $ticketType")
    }

    fun setCurrentUserModal(userId: String, modal: DiscordStepChannelCreationModal) {
        currentUserModals[userId] = modal
    }
}
