package dev.slne.surf.discord.interaction.modal

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.modals.Modal

interface DiscordModal {
    val id: String
    fun create(): Modal
    suspend fun create(hook: InteractionHook): Modal
    suspend fun onSubmit(event: ModalInteractionEvent) {}
}
