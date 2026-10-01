package dev.slne.surf.discord.faq

import java.time.OffsetDateTime

data class FaqUsageKey(
    val faqId: ULong,
    val platform: FaqPlatform
)

data class FaqUsageDelta(
    val count: Long,
    val lastUsedAt: OffsetDateTime
) {
    operator fun plus(other: FaqUsageDelta) = FaqUsageDelta(
        count = count + other.count,
        lastUsedAt = maxOf(lastUsedAt, other.lastUsedAt)
    )
}

class FaqUsageBuffer {
    private val pending = mutableMapOf<FaqUsageKey, FaqUsageDelta>()

    @Synchronized
    fun record(key: FaqUsageKey, usedAt: OffsetDateTime) {
        add(key, FaqUsageDelta(1, usedAt))
    }

    @Synchronized
    fun drain(): Map<FaqUsageKey, FaqUsageDelta> {
        val drained = pending.toMap()
        pending.clear()
        return drained
    }

    @Synchronized
    fun restore(deltas: Map<FaqUsageKey, FaqUsageDelta>) {
        deltas.forEach { (key, delta) -> add(key, delta) }
    }

    private fun add(key: FaqUsageKey, delta: FaqUsageDelta) {
        pending.merge(key, delta, FaqUsageDelta::plus)
    }
}
