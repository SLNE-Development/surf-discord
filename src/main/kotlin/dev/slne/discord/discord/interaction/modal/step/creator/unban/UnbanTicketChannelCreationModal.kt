package dev.slne.discord.discord.interaction.modal.step.creator.unban

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.*
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketPunishmentIdStep
import dev.slne.discord.discord.interaction.modal.step.creator.unban.step.UnbanTicketUnbanAppealStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.util.function.Function

@ChannelCreationModal(ticketType = TicketType.UNBAN)
class UnbanTicketChannelCreationModal : DiscordStepChannelCreationModal(get("modal.unban.title")) {
    override fun buildSteps(): StepBuilder {
        return StepBuilder.Companion.startWith(UnbanTicketPunishmentIdStep())
            .then(Function<ModalStep?, ModalStep> { parent: ModalStep? ->
                UnbanTicketUnbanAppealStep(
                    parent
                )
            })
    }

    override fun getOpenMessages(messages: MessageQueue, channel: TextChannel?, user: User) {
        messages.addMessage(user.asMention)
        messages.addMessage(get("modal.unban.message")!!)
    }
}
