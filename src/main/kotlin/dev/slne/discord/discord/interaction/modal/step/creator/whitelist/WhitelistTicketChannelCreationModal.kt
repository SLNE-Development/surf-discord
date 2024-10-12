package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.MessageQueue
import dev.slne.discord.discord.interaction.modal.step.ModalStep
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

@ChannelCreationModal(ticketType = TicketType.WHITELIST)
class WhitelistTicketChannelCreationModal :
    DiscordStepChannelCreationModal(get("modal.whitelist.title")) {

    override fun buildSteps(): StepBuilder {
        return StepBuilder.startWith(WhitelistTicketConfirmTwitchConnected())
            .then { parent: ModalStep? ->
                WhitelistTicketMinecraftNameStep(
                    parent
                )
            }
    }

    override fun getOpenMessages(messages: MessageQueue, thread: ThreadChannel, user: User) {
        messages.addMessage(user.asMention)
    }
}
