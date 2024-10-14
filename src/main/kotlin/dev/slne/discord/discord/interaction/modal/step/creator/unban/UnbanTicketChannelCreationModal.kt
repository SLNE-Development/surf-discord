package dev.slne.discord.discord.interaction.modal.step.creator.unban

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketPunishmentIdStep
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketUnbanAppealStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.message.translatable
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.UNBAN,
    modalId = UnbanTicketChannelCreationModal.MODAL_ID
)
class UnbanTicketChannelCreationModal : DiscordStepChannelCreationModal(get("modal.unban.title")) {

    override fun buildSteps(): StepBuilder {
        return StepBuilder.startWith(UnbanTicketPunishmentIdStep())
            .then(::UnbanTicketUnbanAppealStep)
    }

    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(translatable("modal.unban.message"))
    }

    companion object {
        const val MODAL_ID = "unban"
    }
}
