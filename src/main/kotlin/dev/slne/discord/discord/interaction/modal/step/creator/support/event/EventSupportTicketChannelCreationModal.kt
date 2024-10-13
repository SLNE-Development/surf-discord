package dev.slne.discord.discord.interaction.modal.step.creator.support.event

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.support.MinecraftSupportMinecraftNameStep
import dev.slne.discord.discord.interaction.modal.step.creator.support.SupportInputStep
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.EVENT_SUPPORT,
    modalId = EventSupportTicketChannelCreationModal.MODAL_ID
)
class EventSupportTicketChannelCreationModal :
    DiscordStepChannelCreationModal(translatable("modal.support.event.titel")) {

    override fun buildSteps(): StepBuilder =
        StepBuilder.startWith(MinecraftSupportMinecraftNameStep())
            .then { SupportInputStep() }

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(user.asMention)
        addMessage(translatable("modal.support.event.message"))
    }

    companion object {
        const val MODAL_ID = "event-support"
    }
}