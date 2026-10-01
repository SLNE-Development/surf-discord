package dev.slne.surf.discord.faq

import java.io.InputStreamReader
import java.util.*

data class FaqSeedEntry(
    val key: String,
    val translations: Map<FaqLocale, FaqTranslation>
)

object FaqSeed {
    private const val RESOURCE = "faq-seed.properties"
    private val fields = setOf("question", "long", "short")
    private val propertyPattern = Regex("^([a-z0-9-]+)\\.([a-z]+)\\.([a-z]+)$")

    fun load(): List<FaqSeedEntry> {
        val resource = javaClass.classLoader.getResourceAsStream(RESOURCE)
            ?: error("$RESOURCE resource not found")

        val props = Properties()
        resource.use { stream ->
            InputStreamReader(stream, Charsets.UTF_8).use { reader -> props.load(reader) }
        }

        return parse(props)
    }

    fun parse(props: Properties): List<FaqSeedEntry> {
        val keys = props.stringPropertyNames().map { name ->
            val match = propertyPattern.matchEntire(name)
                ?: error("Unexpected FAQ seed property '$name'")
            val (key, locale, field) = match.destructured

            check(FaqLocale.fromCode(locale) != null) { "Unknown locale in FAQ seed property '$name'" }
            check(field in fields) { "Unknown field in FAQ seed property '$name'" }

            key
        }.toSortedSet()

        return keys.map { key ->
            FaqSeedEntry(
                key = key,
                translations = FaqLocale.entries.associateWith { locale ->
                    fun field(name: String) = props.getProperty("$key.${locale.code}.$name")
                        ?: error("FAQ seed entry '$key' has no ${locale.code}.$name")

                    FaqTranslation(field("question"), field("long"), field("short"))
                }
            )
        }
    }
}
