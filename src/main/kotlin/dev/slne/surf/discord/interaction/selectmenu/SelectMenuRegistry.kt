package dev.slne.surf.discord.interaction.selectmenu

import dev.slne.surf.discord.interaction.selectmenu.impl.TicketCloseReasonSelectMenu
import dev.slne.surf.discord.interaction.selectmenu.impl.TicketTypeSelectMenu
import dev.slne.surf.discord.interaction.selectmenu.impl.application.ApplicationTypeSelectMenu

object SelectMenuRegistry {
    private val menus = listOf(
        ApplicationTypeSelectMenu, TicketCloseReasonSelectMenu, TicketTypeSelectMenu
    )

    private val menuMap = menus.associateBy { it.id }

    fun getOrNull(id: String) = menuMap[id]
    fun get(id: String) = menuMap[id] ?: error("Select menu with ID '$id' not found!")
    fun all() = menuMap.values.toList()
}
