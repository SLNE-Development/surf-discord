package dev.slne.discordold.discord.interaction.modal.step.creator.unban

import dev.slne.discordold.annotation.ChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.StepBuilder
import dev.slne.discordold.discord.interaction.modal.step.creator.unban.step.UnbanTicketSelectPunishmentTypeStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.punishment.PunishmentNoteService
import dev.slne.discordold.persistence.service.punishment.PunishmentService
import dev.slne.discordold.persistence.service.ticket.TicketService
import dev.slne.discordold.ticket.TicketChannelHelper
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.TicketType
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
