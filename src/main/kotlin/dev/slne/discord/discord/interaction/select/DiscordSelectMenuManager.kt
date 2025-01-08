package dev.slne.discord.discord.interaction.select

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Component
class DiscordSelectMenuManager {

    private val staticMenus: MutableList<DiscordSelectMenu> = mutableListOf()
    private val menus: Cache<String, DiscordSelectMenu> =
        Caffeine.newBuilder().expireAfterWrite(10.minutes.toJavaDuration()).build()

    @PostConstruct
    fun init() {
    }

    fun addMenu(menu: DiscordSelectMenu) = menus.put(menu.id, menu)

    fun addStaticMenu(menu: DiscordSelectMenu) = staticMenus.add(menu)

    fun getMenu(id: String) = staticMenus.firstOrNull { it.id == id } ?: menus.getIfPresent(id)
}
