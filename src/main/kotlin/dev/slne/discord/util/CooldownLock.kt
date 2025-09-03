package dev.slne.discord.util

import kotlin.time.Duration

class CooldownLock(val cooldown: Duration) {

    fun cooldown(key: String): Duration {
        return Duration.ZERO
    }

    fun acquire(key: String): Boolean {
        return true
    }

    fun release(key: String) {

    }
}