package dev.slne.surf.discord.faq

import dev.slne.surf.discord.faq.database.FaqRepository
import dev.slne.surf.discord.logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

object FaqService {
    val USAGE_FLUSH_INTERVAL = 30.seconds

    private val cacheDuration = 5.minutes
    private val cacheMutex = Mutex()
    private val flushMutex = Mutex()
    private val usageBuffer = FaqUsageBuffer()

    @Volatile
    private var cachedEntries: List<FaqEntry>? = null

    @Volatile
    private var cachedAt = TimeSource.Monotonic.markNow()

    suspend fun all(): List<FaqEntry> {
        cachedEntries?.takeIf { cachedAt.elapsedNow() < cacheDuration }?.let { return it }

        return cacheMutex.withLock {
            cachedEntries?.takeIf { cachedAt.elapsedNow() < cacheDuration } ?: reload()
        }
    }

    suspend fun get(key: String): FaqEntry? = all().find { it.key == key }

    suspend fun getActive(key: String, platform: FaqPlatform): FaqEntry? =
        get(key)?.takeIf { it.isActive(platform) }

    suspend fun search(input: String, platform: FaqPlatform): List<FaqEntry> =
        searchFaqEntries(all(), input, platform)

    fun recordUsage(entry: FaqEntry, platform: FaqPlatform) {
        usageBuffer.record(FaqUsageKey(entry.id, platform), OffsetDateTime.now(ZoneOffset.UTC))
    }

    suspend fun flushUsage() = flushMutex.withLock {
        val deltas = usageBuffer.drain()
        if (deltas.isEmpty()) return@withLock

        try {
            FaqRepository.addUsage(deltas)
        } catch (e: Exception) {
            usageBuffer.restore(deltas)
            logger.error("Failed to flush FAQ usage, will retry", e)
        }
    }

    suspend fun seedIfEmpty() {
        if (!FaqRepository.isEmpty()) return

        val entries = FaqSeed.load()
        entries.forEach { FaqRepository.create(it.key, it.translations) }
        invalidate()

        logger.info("Seeded ${entries.size} FAQ entries.")
    }

    private suspend fun reload(): List<FaqEntry> {
        val entries = FaqRepository.findAll().sortedBy { it.key }

        cachedEntries = entries
        cachedAt = TimeSource.Monotonic.markNow()

        return entries
    }

    private fun invalidate() {
        cachedEntries = null
    }
}
