package dev.slne.discord.cooldown

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

class CooldownManager {
    private val cooldown: Object2ObjectMap<CooldownKey, Long> = Object2ObjectOpenHashMap()

    fun setCooldown(
        channelId: Long,
        cooldownDuration: CooldownDuration
    ) {
        cooldown[CooldownKey(channelId, cooldownDuration.name)] = System.currentTimeMillis() + cooldownDuration.cooldown
    }

    fun isOnCooldown(channelId: Long, command: String): Boolean {
        val key = CooldownKey(channelId, command)
        val activeCooldown = cooldown[key] ?: return false
        val now = System.currentTimeMillis()

        if (now >= activeCooldown) {
            cooldown.remove(key)
            return false
        }

        return true
    }
}