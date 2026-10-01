package dev.slne.surf.discord.faq

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FaqSeedTest {
    private val legacyKeys = setOf(
        "connect-twitch", "banned", "next-event", "how-to-open-ticket", "rulebook",
        "server-modpack", "problem-resourcepack", "problem-nrc-voice-chat", "problem-chat",
        "problem-connection", "read-the-docs", "maintenance", "how-to-share-log", "clan-info",
        "take-part-in-event", "how-to-join", "ask", "missing-information", "how-to-whitelist",
        "ping-pong"
    )

    private val markdown = listOf("**", "`", "](", "-# ", "## ", "<#", "\n")

    private fun completeProps() = Properties().apply {
        FaqLocale.entries.forEach { locale ->
            setProperty("ask.${locale.code}.question", "Frage ${locale.code}")
            setProperty("ask.${locale.code}.long", "Lang ${locale.code}")
            setProperty("ask.${locale.code}.short", "Kurz ${locale.code}")
        }
    }

    @Test
    fun `parses all locales and fields`() {
        val entry = FaqSeed.parse(completeProps()).single()

        assertEquals("ask", entry.key)
        assertEquals(FaqTranslation("Frage de", "Lang de", "Kurz de"), entry.translations[FaqLocale.DE])
        assertEquals(FaqTranslation("Frage en", "Lang en", "Kurz en"), entry.translations[FaqLocale.EN])
    }

    @Test
    fun `rejects entries with a missing field`() {
        val props = completeProps().apply { remove("ask.en.short") }

        assertFailsWith<IllegalStateException> { FaqSeed.parse(props) }
    }

    @Test
    fun `rejects unknown locales and fields`() {
        assertFailsWith<IllegalStateException> {
            FaqSeed.parse(completeProps().apply { setProperty("ask.fr.question", "Question") })
        }
        assertFailsWith<IllegalStateException> {
            FaqSeed.parse(completeProps().apply { setProperty("ask.de.title", "Titel") })
        }
        assertFailsWith<IllegalStateException> {
            FaqSeed.parse(completeProps().apply { setProperty("ask.question", "Frage") })
        }
    }

    @Test
    fun `bundled seed contains all legacy entries in every locale with valid content`() {
        val entries = FaqSeed.load()

        assertEquals(legacyKeys, entries.map { it.key }.toSet())
        entries.forEach { entry ->
            assertTrue(FaqLimits.isValidKey(entry.key), entry.key)
            assertEquals(FaqLocale.entries.toSet(), entry.translations.keys, entry.key)
            entry.translations.forEach { (locale, translation) ->
                assertTrue(FaqLimits.isValid(translation), "${entry.key} ${locale.code}")
            }
        }
    }

    @Test
    fun `bundled short texts contain no markdown`() {
        FaqSeed.load().forEach { entry ->
            entry.translations.forEach { (locale, translation) ->
                markdown.forEach {
                    assertFalse(it in translation.shortText, "${entry.key} ${locale.code} contains '$it'")
                }
            }
        }
    }

    @Test
    fun `bundled seed keeps multiline answers complete`() {
        val pingPong = FaqSeed.load().single { it.key == "ping-pong" }

        assertTrue(
            pingPong.translations.getValue(FaqLocale.DE).longText
                .endsWith("Vielen Dank für euer Verständnis und eure Rücksichtnahme.")
        )
        assertTrue(
            pingPong.translations.getValue(FaqLocale.EN).longText
                .endsWith("Thank you for your understanding and consideration.")
        )
    }
}
