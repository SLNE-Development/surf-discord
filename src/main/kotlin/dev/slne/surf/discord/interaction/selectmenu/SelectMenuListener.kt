package dev.slne.surf.discord.interaction.selectmenu

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class SelectMenuListener(
    private val jda: JDA,
    private val registry: SelectMenuRegistry,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {

    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        val menu = registry.get(event.componentId)
        discordScope.launch {
            menu.onSelect(event)
        }
    }
}
