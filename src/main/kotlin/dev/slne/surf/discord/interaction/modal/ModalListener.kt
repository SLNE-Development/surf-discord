package dev.slne.surf.discord.interaction.modal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class ModalListener(
    private val registry: ModalRegistry,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {
    override fun onModalInteraction(event: ModalInteractionEvent) {
        discordScope.launch {
            registry.get(event.modalId).onSubmit(event)
        }
    }
}
