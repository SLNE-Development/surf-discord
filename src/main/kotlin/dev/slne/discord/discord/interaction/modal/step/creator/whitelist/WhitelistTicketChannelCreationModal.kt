package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.*
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.util.function.Function

@ChannelCreationModal(ticketType = TicketType.WHITELIST)
class WhitelistTicketChannelCreationModal protected constructor() : DiscordStepChannelCreationModal(
    get("modal.whitelist.title")
) {
    override fun buildSteps(): StepBuilder {
        return StepBuilder.Companion.startWith(WhitelistTicketConfirmTwitchConnected())
            .then(Function<ModalStep?, ModalStep> { parent: ModalStep? ->
                WhitelistTicketMinecraftNameStep(
                    parent
                )
            })
    }

    override fun getOpenMessages(messages: MessageQueue, channel: TextChannel?, user: User) {
        messages.addMessage(user.asMention)
    }
}
