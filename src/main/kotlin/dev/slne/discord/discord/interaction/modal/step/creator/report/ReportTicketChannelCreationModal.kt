package dev.slne.discord.discord.interaction.modal.step.creator.report

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.ReportTicketSelectTypeStep
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.REPORT,
    modalId = ReportTicketChannelCreationModal.MODAL_ID
)
class ReportTicketChannelCreationModal(
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper,
    discordModalManager: DiscordModalManager,
    private val userService: UserService,
    private val whitelistService: WhitelistService,
    private val messageManager: MessageManager
) : DiscordStepChannelCreationModal(
    translatable("modal.report.title"),
    ticketCreator,
    ticketChannelHelper,
    discordModalManager,
) {

    override fun buildSteps(): StepBuilder {
        return StepBuilder.startWith(
            ReportTicketSelectTypeStep(
                userService,
                whitelistService,
                messageManager
            )
        )
    }

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.report.message"))
    }

    companion object {
        const val MODAL_ID = "report"
    }
}
