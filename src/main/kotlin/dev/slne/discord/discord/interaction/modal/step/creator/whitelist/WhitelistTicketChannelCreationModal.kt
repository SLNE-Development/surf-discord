package dev.slne.discord.discord.interaction.modal.step.creator.whitelist

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.discord.interaction.modal.step.StepBuilder
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketConfirmTwitchConnected
import dev.slne.discord.discord.interaction.modal.step.creator.whitelist.step.WhitelistTicketMinecraftNameStep
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.TicketType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction

@ChannelCreationModal(
    ticketType = TicketType.WHITELIST,
    modalId = WhitelistTicketChannelCreationModal.MODAL_ID
)
class WhitelistTicketChannelCreationModal(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    ticketCreator: TicketCreator,
    ticketChannelHelper: TicketChannelHelper, discordModalManager: DiscordModalManager
) : DiscordStepChannelCreationModal(
    translatable("modal.whitelist.title"),
    ticketCreator,
    ticketChannelHelper,
    discordModalManager
) {

    override fun buildSteps() = StepBuilder.startWith(WhitelistTicketConfirmTwitchConnected())
        .then { WhitelistTicketMinecraftNameStep(it, userService, whitelistService) }

    override suspend fun preStartCreationValidation(
        interaction: StringSelectInteraction,
        guild: Guild
    ) {
        val user = interaction.user
        if (whitelistService.isWhitelisted(user)) {
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
