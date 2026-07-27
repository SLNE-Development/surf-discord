package dev.slne.surf.discord.interaction.selectmenu

import dev.slne.surf.discord.discordScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object SelectMenuListener : ListenerAdapter() {
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        discordScope.launch {
            SelectMenuRegistry.getOrNull(event.componentId)?.onSelect(event)
        }
    }
}
