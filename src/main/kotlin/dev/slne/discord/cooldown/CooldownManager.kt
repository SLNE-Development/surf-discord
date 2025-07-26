package dev.slne.discord.cooldown

import dev.slne.discord.message.translatable
import java.util.concurrent.ConcurrentHashMap

object CooldownManager {
    private val cooldownManager: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    fun setCooldown(
        command: String,
        channelId: String,
        cooldown: Long
    ) {
        cooldownManager["$command:$channelId"] = System.currentTimeMillis() + cooldown
    }

    fun isOnCooldown(command: String, channelId: String): String? {
        val key = "$command:$channelId"
        val now = System.currentTimeMillis()
        val expiresAt = cooldownManager[key] ?: return null

        return if (now < expiresAt) {
            val remaining = (expiresAt - now) / 1000
            translatable("interaction.command.cooldown.active", remaining.toString())
        } else {
            cooldownManager.remove(key)
            null
        }
    }
}