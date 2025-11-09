package dev.slne.surf.discord.interaction.selectmenu

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class SelectMenuListener(
    private val registry: SelectMenuRegistry,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        discordScope.launch {
            registry.get(event.componentId).onSelect(event)
        }
    }
}
