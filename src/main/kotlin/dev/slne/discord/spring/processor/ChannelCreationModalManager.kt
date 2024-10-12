package dev.slne.discord.spring.processor

import dev.slne.discord.annotation.ChannelCreationModal
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.step.DiscordStepChannelCreationModal
import dev.slne.discord.ticket.TicketType

object ChannelCreationModalManager {

    fun register(modalId: String, supplier: () -> DiscordStepChannelCreationModal) =
        DiscordModalManager.registerAdvancedModal(modalId, supplier)

    fun getModalId(annotation: ChannelCreationModal) =
        annotation.modalId.ifEmpty { annotation.ticketType.name }

    fun getTicketType(annotation: ChannelCreationModal): TicketType = annotation.ticketType
}
