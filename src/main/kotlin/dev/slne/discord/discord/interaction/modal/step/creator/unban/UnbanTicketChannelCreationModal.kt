package dev.slne.discord.discord.interaction.modal.step.creator.unban

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketSelectPunishmentTypeStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.punishment.PunishmentNoteService
import dev.slne.discord.persistence.service.punishment.PunishmentService
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.UNBAN,
    modalId = UnbanTicketChannelCreationModal.MODAL_ID
)
class UnbanTicketChannelCreationModal(
    private val punishmentService: PunishmentService,
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper,
    discordModalManager: DiscordModalManager,
    private val punishmentNoteService: PunishmentNoteService,
    private val ticketService: TicketService
) : DiscordStepChannelCreationModal(
    translatable("modal.unban.title"),
    ticketCreator,
    ticketChannelHelper,
    discordModalManager,
) {

    override fun buildSteps(): StepBuilder {
        return StepBuilder.startWith(
            UnbanTicketSelectPunishmentTypeStep(
                punishmentService,
                ticketService,
                punishmentNoteService
            )
        )
    }

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.unban.message"))
    }

    companion object {
        const val MODAL_ID = "unban"
    }
}
