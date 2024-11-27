package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.whitelist.WhitelistRepository
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@ChannelCreationModal(
    ticketType = TicketType.WHITELIST,
    modalId = WhitelistTicketChannelCreationModal.MODAL_ID
)
class WhitelistTicketChannelCreationModal :
    DiscordStepChannelCreationModal(translatable("modal.whitelist.title")) {

    override fun buildSteps() = StepBuilder.startWith(WhitelistTicketConfirmTwitchConnected())
        .then(::WhitelistTicketMinecraftNameStep)

    override suspend fun preStartCreationValidation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        val user = interaction.user
        if (WhitelistRepository.isWhitelisted(user)) {
            throw PreThreadCreationException(translatable("error.ticket.whitelist.already-whitelisted"))
        }

//        val connections = user.retrieveConnections().await()
//        val twitchConnection = connections.find { it.type == "twitch" }
//
//        println("Twitch connection: $twitchConnection")
//
//        if (twitchConnection == null || !twitchConnection.verified) {
//            throw PreThreadCreationException(translatable("error.ticket.whitelist.twitch-not-connected"))
//        }
//
//        if (twitchConnection.visibility != UserConnection.Visibility.EVERYONE) {
//            throw PreThreadCreationException(translatable("error.ticket.whitelist.twitch-not-public"))
//        }
    }

    companion object {
        const val MODAL_ID = "whitelist_ticket"
    }
}
