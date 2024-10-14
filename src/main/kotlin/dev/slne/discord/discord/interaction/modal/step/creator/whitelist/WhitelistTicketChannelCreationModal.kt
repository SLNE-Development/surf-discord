package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.RawMessages.Companion.get
import dev.slne.discord.message.translatable
import dev.slne.discord.spring.service.whitelist.WhitelistService
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@ChannelCreationModal(
    ticketType = TicketType.WHITELIST,
    modalId = WhitelistTicketChannelCreationModal.MODAL_ID
)
class WhitelistTicketChannelCreationModal :
    DiscordStepChannelCreationModal(get("modal.whitelist.title")) {

    override fun buildSteps() = StepBuilder.startWith(WhitelistTicketConfirmTwitchConnected())
        .then(::WhitelistTicketMinecraftNameStep)

    override suspend fun preStartCreationValidation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        if (WhitelistService.isWhitelisted(interaction.user)) {
            throw PreThreadCreationException(translatable("error.ticket.whitelist.already-whitelisted"))
        }
    }

    companion object {
        const val MODAL_ID = "whitelist_ticket"
    }
}
