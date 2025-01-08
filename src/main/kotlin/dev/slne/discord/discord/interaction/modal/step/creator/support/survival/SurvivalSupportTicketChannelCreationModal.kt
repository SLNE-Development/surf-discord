package dev.slne.discord.discord.interaction.modal.step.creator.support.survival

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discord.getBean
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@ChannelCreationModal(
    ticketType = TicketType.SURVIVAL_SUPPORT,
    modalId = SurvivalSupportTicketChannelCreationModal.MODAL_ID
)
class SurvivalSupportTicketChannelCreationModal :
    DiscordStepChannelCreationModal(translatable("modal.support.survival.titel")) {

    override fun buildSteps(): StepBuilder = StepBuilder.startWith(SupportInputStep())

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.support.survival.message"))
    }

    override suspend fun preStartCreationValidation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        val user = interaction.user

        if (!getBean<WhitelistService>().isWhitelisted(user)) {
            throw PreThreadCreationException(translatable("error.ticket.whitelist.not-whitelisted"))
        }
    }

    companion object {
        const val MODAL_ID = "survival-support"
    }
}