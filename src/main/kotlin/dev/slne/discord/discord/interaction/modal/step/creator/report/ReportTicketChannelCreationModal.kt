package dev.slne.discord.discord.interaction.modal.step.creator.report

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.report.steps.ReportTicketSelectTypeStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(ticketType = TicketType.REPORT)
class ReportTicketChannelCreationModal :
    DiscordStepChannelCreationModal(get("modal.report.title")) {
        
    override fun buildSteps(): StepBuilder {
        return StepBuilder.startWith(ReportTicketSelectTypeStep())
    }

    override fun getOpenMessages(messages: MessageQueue, thread: ThreadChannel, user: User) {
        messages.addMessage(user.asMention)
        messages.addMessage(get("modal.report.message"))
    }
}
