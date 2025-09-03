package dev.slne.discord.util

import kotlinx.coroutines.*
import java.lang.AutoCloseable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class CommandLock(
    private val emergencyReleaseAfter: Duration = 5.minutes,
    coroutineContext: CoroutineContext = Dispatchers.Default
) : AutoCloseable {

    private val locks = ConcurrentHashMap<Long, Entry>()
    private val scope = CoroutineScope(SupervisorJob() + coroutineContext)

    fun acquire(channelId: Long): Boolean {
        val now = System.nanoTime()
        val durationNanos = emergencyReleaseAfter.inWholeNanoseconds
        var acquired = false

        locks.compute(channelId) { _, existing ->
            if (existing == null || now >= existing.deadlineNanos) {
                existing?.job?.cancel()

                val token = UUID.randomUUID()
                val deadline = now + durationNanos

                val job = scope.launch {
                    delay(emergencyReleaseAfter)
                    locks.compute(channelId) { _, cur ->
                        if (cur?.token == token && cur.deadlineNanos == deadline) null else cur
                    }
                }

                acquired = true
                Entry(token = token, deadlineNanos = deadline, job = job)
            } else {
                existing
            }
        }

        return acquired
    }

    fun release(channelId: Long) {
        locks.computeIfPresent(channelId) { _, entry ->
            entry.job.cancel()
            null
        }
    }

    override fun close() {
        locks.keys.forEach { release(it) }
        scope.cancel()
    }

    private data class Entry(
        val token: UUID,
        val deadlineNanos: Long,
        val job: Job
    )
}