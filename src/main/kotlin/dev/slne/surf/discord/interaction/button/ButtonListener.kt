package dev.slne.surf.discord.interaction.button

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class ButtonListener(
    private val jda: JDA,
    private val registry: ButtonRegistry,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val button = registry.get(event.componentId)
        discordScope.launch {
            button.onClick(event)
        }
    }
}
