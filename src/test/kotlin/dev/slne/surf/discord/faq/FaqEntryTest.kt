package dev.slne.surf.discord.faq

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FaqEntryTest {
    private fun entry(
        key: String,
        question: String = key,
        activeDiscord: Boolean = true,
        activeMinecraft: Boolean = true,
        activePanel: Boolean = true
    ) = FaqEntry(
        0u, key, activeDiscord, activeMinecraft, activePanel,
        mapOf(
            FaqLocale.DE to FaqTranslation(question, "lang", "kurz"),
            FaqLocale.EN to FaqTranslation("en $question", "long", "short")
        )
    )

    @Test
    fun `accepts lowercase keys with digits and dashes`() {
        assertTrue(FaqLimits.isValidKey("how-to-join"))
        assertTrue(FaqLimits.isValidKey("a"))
        assertTrue(FaqLimits.isValidKey("problem-2"))
        assertTrue(FaqLimits.isValidKey("a".repeat(64)))
    }

    @Test
    fun `rejects invalid keys`() {
        assertFalse(FaqLimits.isValidKey(""))
        assertFalse(FaqLimits.isValidKey("How-To-Join"))
        assertFalse(FaqLimits.isValidKey("how to join"))
        assertFalse(FaqLimits.isValidKey("faq:key"))
        assertFalse(FaqLimits.isValidKey("über"))
        assertFalse(FaqLimits.isValidKey("a".repeat(65)))
    }

    @Test
    fun `validates translation lengths`() {
        assertTrue(FaqLimits.isValid(FaqTranslation("q", "l", "s")))
        assertFalse(FaqLimits.isValid(FaqTranslation("", "l", "s")))
        assertFalse(FaqLimits.isValid(FaqTranslation("q", "l", "s".repeat(513))))
        assertFalse(FaqLimits.isValid(FaqTranslation("q", "l".repeat(3601), "s")))
    }

    @Test
    fun `translation falls back to german`() {
        val germanOnly = FaqEntry(
            0u, "ask", true, true, true,
            mapOf(FaqLocale.DE to FaqTranslation("Frage", "lang", "kurz"))
        )

        assertEquals("Frage", germanOnly.translation(FaqLocale.EN).question)
        assertEquals("en ask", entry("ask").translation(FaqLocale.EN).question)
    }

    @Test
    fun `search matches key and german question case insensitively`() {
        val entries = listOf(
            entry("rulebook", "Regelwerk"),
            entry("how-to-join", "Wie trete ich bei?"),
            entry("banned", "Ich wurde gebannt")
        )

        assertEquals(listOf("rulebook"), searchFaqEntries(entries, "REGEL", FaqPlatform.DISCORD).map { it.key })
        assertEquals(listOf("how-to-join"), searchFaqEntries(entries, "join", FaqPlatform.DISCORD).map { it.key })
    }

    @Test
    fun `search ranks key prefix matches first`() {
        val entries = listOf(
            entry("problem-chat", "Chat"),
            entry("ask", "Frag bei problem direkt")
        )

        assertEquals(
            listOf("problem-chat", "ask"),
            searchFaqEntries(entries, "problem", FaqPlatform.DISCORD).map { it.key }
        )
    }

    @Test
    fun `search filters by platform`() {
        val entries = listOf(
            entry("both"),
            entry("minecraft-only", activeDiscord = false),
            entry("discord-only", activeMinecraft = false),
            entry("panel-off", activePanel = false)
        )

        assertEquals(
            listOf("both", "discord-only", "panel-off"),
            searchFaqEntries(entries, "", FaqPlatform.DISCORD).map { it.key }
        )
        assertEquals(
            listOf("both", "minecraft-only", "panel-off"),
            searchFaqEntries(entries, "", FaqPlatform.MINECRAFT).map { it.key }
        )
        assertEquals(
            listOf("both", "discord-only", "minecraft-only"),
            searchFaqEntries(entries, "", FaqPlatform.PANEL).map { it.key }
        )
    }

    @Test
    fun `search returns at most 25 entries`() {
        val entries = (1..40).map { entry("entry-$it") }

        assertEquals(25, searchFaqEntries(entries, "entry", FaqPlatform.DISCORD).size)
    }
}
