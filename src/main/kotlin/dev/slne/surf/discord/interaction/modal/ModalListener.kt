package dev.slne.surf.discord.interaction.modal

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class ModalListener(
    private val jda: JDA,
    private val registry: ModalRegistry,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        val modal = registry.get(event.modalId) ?: return
        discordScope.launch {
            modal.onSubmit(event)
        }
    }
}
