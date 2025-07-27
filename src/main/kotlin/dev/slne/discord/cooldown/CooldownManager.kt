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

    fun getRemainingMillis(cooldownKey: CooldownKey): Long {
        val activeCooldown = cooldown[cooldownKey] ?: return 0L
        val now = System.currentTimeMillis()

        return if (now < activeCooldown) {
            activeCooldown - now
        } else {
            cooldown.remove(cooldownKey)
            0L
        }
    }

}

data class CooldownKey(val channelId: Long, val command: String)