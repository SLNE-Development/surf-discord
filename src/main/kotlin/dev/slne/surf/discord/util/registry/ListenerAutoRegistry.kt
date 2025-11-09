package dev.slne.surf.discord.util.registry

import dev.slne.surf.discord.jda
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class ListenerAutoRegistry(
    private val listeners: ObjectProvider<ListenerAdapter>
) {
    @PostConstruct
    fun registerListeners() {
        listeners.forEach(jda::addEventListener)
    }
}