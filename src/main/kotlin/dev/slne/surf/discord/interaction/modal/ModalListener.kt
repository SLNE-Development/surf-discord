package dev.slne.surf.discord.interaction.modal

import dev.slne.surf.discord.discordScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ModalListener : ListenerAdapter() {
    override fun onModalInteraction(event: ModalInteractionEvent) {
        discordScope.launch {
            ModalRegistry.getOrNull(event.modalId)?.onSubmit(event)
        }
    }
}
