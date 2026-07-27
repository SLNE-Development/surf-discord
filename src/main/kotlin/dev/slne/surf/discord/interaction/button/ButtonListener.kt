package dev.slne.surf.discord.interaction.button

import dev.slne.surf.discord.discordScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object ButtonListener : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val button = ButtonRegistry.getOrNull(event.componentId) ?: return
        discordScope.launch {
            button.onClick(event)
        }
    }
}
