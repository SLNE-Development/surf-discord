package dev.slne.discord.discord.interaction.modal.step.creator.support.discord

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.SURVIVAL_SUPPORT,
    modalId = DiscordSupportTicketChannelCreationModal.MODAL_ID
)
class DiscordSupportTicketChannelCreationModal :
    DiscordStepChannelCreationModal(translatable("modal.support.discord.titel")) {

    override fun buildSteps(): StepBuilder = StepBuilder.startWith(SupportInputStep())

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(user.asMention)
        addMessage(translatable("modal.support.discord.message"))
    }

    companion object {
        const val MODAL_ID = "discord-support"
    }
}