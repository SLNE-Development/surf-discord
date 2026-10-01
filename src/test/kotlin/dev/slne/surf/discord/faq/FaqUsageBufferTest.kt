package dev.slne.surf.discord.faq

import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FaqUsageBufferTest {
    private val start = OffsetDateTime.of(2026, 9, 30, 12, 0, 0, 0, ZoneOffset.UTC)
    private val discord = FaqUsageKey(1u, FaqPlatform.DISCORD)
    private val minecraft = FaqUsageKey(1u, FaqPlatform.MINECRAFT)

    @Test
    fun `aggregates usages per entry and platform`() {
        val buffer = FaqUsageBuffer()

        buffer.record(discord, start)
        buffer.record(discord, start.plusSeconds(5))
        buffer.record(minecraft, start.plusSeconds(1))

        assertEquals(
            mapOf(
                discord to FaqUsageDelta(2, start.plusSeconds(5)),
                minecraft to FaqUsageDelta(1, start.plusSeconds(1))
            ),
            buffer.drain()
        )
    }

    @Test
    fun `keeps the latest usage time regardless of order`() {
        val buffer = FaqUsageBuffer()

        buffer.record(discord, start.plusSeconds(10))
        buffer.record(discord, start)

        assertEquals(start.plusSeconds(10), buffer.drain().getValue(discord).lastUsedAt)
    }

    @Test
    fun `drain empties the buffer`() {
        val buffer = FaqUsageBuffer()

        buffer.record(discord, start)
        buffer.drain()

        assertTrue(buffer.drain().isEmpty())
    }

    @Test
    fun `restore merges failed deltas with new usages`() {
        val buffer = FaqUsageBuffer()

        buffer.record(discord, start)
        val failed = buffer.drain()
        buffer.record(discord, start.plusSeconds(3))
        buffer.restore(failed)

        assertEquals(FaqUsageDelta(2, start.plusSeconds(3)), buffer.drain().getValue(discord))
    }
}
