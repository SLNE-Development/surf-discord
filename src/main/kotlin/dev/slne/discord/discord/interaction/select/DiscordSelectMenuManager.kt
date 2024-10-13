package dev.slne.discord.discord.interaction.select

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration

object DiscordSelectMenuManager {

    private val staticMenus: MutableList<DiscordSelectMenu> = mutableListOf()
    private val menus: Cache<String, DiscordSelectMenu> =
        Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(5)).build()

    init {

    }

    fun addMenu(menu: DiscordSelectMenu) = menus.put(menu.id, menu)

    fun addStaticMenu(menu: DiscordSelectMenu) = staticMenus.add(menu)

    fun getMenu(id: String) = staticMenus.firstOrNull { it.id == id } ?: menus.getIfPresent(id)
}
