package dev.slne.discordold.discord.interaction.modal.step.creator.support.survival

import dev.slne.discordold.annotation.ChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.StepBuilder
import dev.slne.discordold.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import dev.slne.discordold.ticket.TicketChannelHelper
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.TicketType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@ChannelCreationModal(
    ticketType = TicketType.SURVIVAL_SUPPORT,
    modalId = SurvivalSupportTicketChannelCreationModal.MODAL_ID
)
class SurvivalSupportTicketChannelCreationModal(
    private val whitelistService: WhitelistService,
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper,
    discordModalManager: DiscordModalManager
) :
    DiscordStepChannelCreationModal(
        translatable("modal.support.survival.titel"),
        ticketCreator,
        ticketChannelHelper,
        discordModalManager
    ) {

    override fun buildSteps(): StepBuilder = StepBuilder.startWith(SupportInputStep())

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.support.survival.message"))
    }

    override suspend fun preStartCreationValidation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        val user = interaction.user

        if (!whitelistService.isWhitelisted(user)) {
            throw PreThreadCreationException(translatable("error.ticket.whitelist.not-whitelisted"))
        }
    }

    companion object {
        const val MODAL_ID = "survival-support"
    }
}