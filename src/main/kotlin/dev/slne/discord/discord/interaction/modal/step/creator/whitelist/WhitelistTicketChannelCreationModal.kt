package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(
    ticketType = TicketType.WHITELIST,
    modalId = WhitelistTicketChannelCreationModal.MODAL_ID
)
class WhitelistTicketChannelCreationModal :
    DiscordStepChannelCreationModal(get("modal.whitelist.title")) {

    override fun buildSteps() = StepBuilder.startWith(WhitelistTicketConfirmTwitchConnected())
        .then(::WhitelistTicketMinecraftNameStep)


    override suspend fun MessageQueue.getOpenMessages(thread: ThreadChannel, user: User) {
        addMessage(user.asMention)
    }

    companion object {
        const val MODAL_ID = "whitelist_ticket"
    }
}
