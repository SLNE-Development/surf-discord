package dev.slne.discordold.discord.interaction.modal.step.creator.support.event

import dev.slne.discordold.annotation.ChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discordold.discord.interaction.modal.step.MessageQueue
import dev.slne.discordold.discord.interaction.modal.step.StepBuilder
import dev.slne.discordold.discord.interaction.modal.step.creator.support.MinecraftSupportMinecraftNameStep
import dev.slne.discordold.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discordold.message.translatable
import dev.slne.discordold.ticket.TicketChannelHelper
import dev.slne.discordold.ticket.TicketCreator
import dev.slne.discordold.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.EVENT_SUPPORT,
    modalId = EventSupportTicketChannelCreationModal.MODAL_ID
)
class EventSupportTicketChannelCreationModal(
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper,
    discordModalManager: DiscordModalManager
) : DiscordStepChannelCreationModal(
    translatable("modal.support.event.titel"),
    ticketCreator,
    ticketChannelHelper,
    discordModalManager,
) {

    override fun buildSteps(): StepBuilder =
        StepBuilder.startWith(MinecraftSupportMinecraftNameStep())
            .then { SupportInputStep() }

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.support.event.message"))
    }

    companion object {
        const val MODAL_ID = "event-support"
    }
}