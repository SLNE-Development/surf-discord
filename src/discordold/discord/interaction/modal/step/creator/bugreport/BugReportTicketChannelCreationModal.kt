package dev.slne.discordold.discord.interaction.modal.step.creator.bugreport

import dev.slne.discordold.annotation.ChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.StepBuilder
import dev.slne.discordold.discord.interaction.modal.step.creator.bugreport.steps.BugReportMinecraftNameStep
import dev.slne.discordold.discord.interaction.modal.step.creator.bugreport.steps.BugReportReportStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.ticket.TicketChannelHelper
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

// wenn whitelist da dann nicht nach mc namen fragen sonst schon
// feld für beschreibung von bug (wie mit bilden? -> einfach danach ins ticket schicken)
@ChannelCreationModal(
    ticketType = TicketType.BUGREPORT,
    modalId = BugReportTicketChannelCreationModal.MODAL_ID
)
class BugReportTicketChannelCreationModal(
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper, discordModalManager: DiscordModalManager
) : DiscordStepChannelCreationModal(
    translatable("modal.bug-report.title"),
    ticketCreator,
    ticketChannelHelper,
    discordModalManager,
) {

    override fun buildSteps(): StepBuilder = StepBuilder.startWith(BugReportMinecraftNameStep())
        .then(::BugReportReportStep)

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.bug-report.message"))
    }

    companion object {
        const val MODAL_ID = "bug-report"
    }
}