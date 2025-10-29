package dev.slne.surf.discord.interaction.modal

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.modals.Modal

interface DiscordModal {
    val id: String
    val modal: Modal

    suspend fun onSubmit(event: ModalInteractionEvent) {}
}
