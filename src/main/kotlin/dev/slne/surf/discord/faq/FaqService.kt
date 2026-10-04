package dev.slne.surf.discord.faq

import dev.slne.surf.discord.faq.database.FaqRepository
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.redis.RedisService
import dev.slne.surf.moderation.tools.faq.redis.FaqsChangedEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FaqService {
    val USAGE_FLUSH_INTERVAL = 30.seconds
    val REFRESH_INTERVAL = 1.minutes

    private val flushMutex = Mutex()
    private val usageBuffer = FaqUsageBuffer()
    private val sendBuffer = FaqSendBuffer()

    @Volatile
    private var cachedEntries: List<FaqEntry> = emptyList()

    fun all(): List<FaqEntry> = cachedEntries

    fun get(key: String): FaqEntry? = all().find { it.key == key }

    fun getActive(key: String, platform: FaqPlatform): FaqEntry? =
        get(key)?.takeIf { it.isActive(platform) }

    fun search(input: String, platform: FaqPlatform): List<FaqEntry> =
        searchFaqEntries(all(), input, platform)

    fun recordUsage(entry: FaqEntry, platform: FaqPlatform, sender: FaqSender) {
        val now = OffsetDateTime.now(ZoneOffset.UTC)

        usageBuffer.record(FaqUsageKey(entry.id, platform), now)
        sendBuffer.record(FaqSend(entry.id, platform, sender, now))
    }

    suspend fun flushUsage() = flushMutex.withLock {
        flushUsageDeltas()
        flushSends()
    }

    private suspend fun flushUsageDeltas() {
        val deltas = usageBuffer.drain()
        if (deltas.isEmpty()) return

        try {
            FaqRepository.addUsage(deltas)
        } catch (e: Exception) {
            usageBuffer.restore(deltas)
            logger.error("Failed to flush FAQ usage, will retry", e)
        }
    }

    private suspend fun flushSends() {
        val sends = sendBuffer.drain()
        if (sends.isEmpty()) return

        try {
            FaqRepository.addSends(sends)
        } catch (e: Exception) {
            sendBuffer.restore(sends)
            logger.error("Failed to flush FAQ sends, will retry", e)
        }
    }

    suspend fun seedIfEmpty() {
        if (!FaqRepository.isEmpty()) return

        val entries = FaqSeed.load()
        entries.forEach { FaqRepository.create(it.key, it.translations) }

        logger.info("Seeded ${entries.size} FAQ entries.")
    }

    suspend fun load() {
        val entries = FaqRepository.findAll().sortedBy { it.key }
        val changed = entries != cachedEntries
        cachedEntries = entries

        if (changed) {
            RedisService.publishEvent(FaqsChangedEvent())
        }
    }

    suspend fun refresh() {
        try {
            load()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to refresh FAQ entries, keeping the cached ones", e)
        }
    }
}
