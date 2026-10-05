package dev.slne.surf.discord.faq

import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FaqSendBufferTest {
    private val start = OffsetDateTime.of(2026, 10, 4, 12, 0, 0, 0, ZoneOffset.UTC)
    private val discordSend = FaqSend(1u, FaqPlatform.DISCORD, FaqSender("123", "discord-user"), start)
    private val minecraftSend = FaqSend(
        1u,
        FaqPlatform.MINECRAFT,
        FaqSender("00000000-0000-0000-0000-000000000001", "MinecraftUser"),
        start.plusSeconds(1)
    )

    @Test
    fun `keeps every send separately in order`() {
        val buffer = FaqSendBuffer()

        buffer.record(discordSend)
        buffer.record(discordSend)
        buffer.record(minecraftSend)

        assertEquals(listOf(discordSend, discordSend, minecraftSend), buffer.drain())
    }

    @Test
    fun `drain empties the buffer`() {
        val buffer = FaqSendBuffer()

        buffer.record(discordSend)
        buffer.drain()

        assertTrue(buffer.drain().isEmpty())
    }

    @Test
    fun `restore puts failed sends before new ones`() {
        val buffer = FaqSendBuffer()

        buffer.record(discordSend)
        val failed = buffer.drain()
        buffer.record(minecraftSend)
        buffer.restore(failed)

        assertEquals(listOf(discordSend, minecraftSend), buffer.drain())
    }
}
