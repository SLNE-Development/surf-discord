package dev.slne.surf.discord.interaction.button

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
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        println("Button interaction received: ${event.componentId}")
        val button = registry.getOrNull(event.componentId) ?: return
        println("Button found: ${button.javaClass.simpleName}")
        discordScope.launch {
            println("Handling button click for: ${button.javaClass.simpleName}")
            button.onClick(event)
            println("Button click handled: ${button.javaClass.simpleName}")
        }
    }
}
