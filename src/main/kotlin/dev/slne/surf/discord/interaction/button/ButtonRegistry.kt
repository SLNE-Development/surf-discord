package dev.slne.surf.discord.interaction.button

import dev.slne.surf.discord.interaction.button.impl.WhitelistCreateButton

object ButtonRegistry {
    private val buttons = listOf(
        WhitelistCreateButton
    )

    private val buttonMap = buttons.associateBy { it.id }

    fun getOrNull(id: String) = buttonMap[id]
    fun get(id: String) = buttonMap[id] ?: error("Button with ID $id not found in registry!")
    fun all() = buttonMap.values.toList()
}
