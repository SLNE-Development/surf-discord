package dev.slne.surf.discord.interaction.button

import org.springframework.stereotype.Component

// TODO: Replace this with the button registrar?
@Component
class ButtonRegistry(buttons: List<DiscordButton>) {
    private val buttonMap = buttons.associateBy { it.id }

    fun get(id: String) = buttonMap[id] ?: error("Button with ID $id not found in registry!")
    fun all() = buttonMap.values.toList()
}
