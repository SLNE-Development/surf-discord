package dev.slne.discord.discord.interaction.modal.step.creator.support.discord

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.DISCORD_SUPPORT,
    modalId = DiscordSupportTicketChannelCreationModal.MODAL_ID
)
class DiscordSupportTicketChannelCreationModal(
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper,
    discordModalManager: DiscordModalManager
) :
    DiscordStepChannelCreationModal(
        translatable("modal.support.discord.titel"),
        ticketCreator,
        ticketChannelHelper,
        discordModalManager
    ) {

    override fun buildSteps(): StepBuilder = StepBuilder.startWith(SupportInputStep())

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.support.discord.message"))
    }

    companion object {
        const val MODAL_ID = "discord-support"
    }
}