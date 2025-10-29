package dev.slne.surf.discord.interaction.selectmenu

import org.springframework.stereotype.Component

@Component
class SelectMenuRegistry(menus: List<DiscordSelectMenu>) {
    private val menuMap = menus.associateBy { it.id }

    fun get(id: String) = menuMap[id] ?: error("Select menu with ID '$id' not found!")
    fun all() = menuMap.values.toList()
}
