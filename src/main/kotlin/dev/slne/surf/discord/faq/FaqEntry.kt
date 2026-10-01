package dev.slne.surf.discord.faq

import net.dv8tion.jda.api.interactions.commands.build.OptionData

enum class FaqLocale(val code: String) {
    DE("de"),
    EN("en");

    companion object {
        val DEFAULT = DE

        fun fromCode(code: String) = entries.find { it.code == code }
    }
}

enum class FaqPlatform {
    DISCORD,
    MINECRAFT,
    PANEL
}

data class FaqTranslation(
    val question: String,
    val longText: String,
    val shortText: String
)

data class FaqEntry(
    val id: ULong,
    val key: String,
    val activeDiscord: Boolean,
    val activeMinecraft: Boolean,
    val activePanel: Boolean,
    val translations: Map<FaqLocale, FaqTranslation>
) {
    fun isActive(platform: FaqPlatform) = when (platform) {
        FaqPlatform.DISCORD -> activeDiscord
        FaqPlatform.MINECRAFT -> activeMinecraft
        FaqPlatform.PANEL -> activePanel
    }

    fun translation(locale: FaqLocale = FaqLocale.DEFAULT): FaqTranslation =
        translations[locale] ?: translations.getValue(FaqLocale.DEFAULT)
}

object FaqLimits {
    const val MAX_KEY_LENGTH = 64
    const val MAX_QUESTION_LENGTH = 256
    const val MAX_LONG_TEXT_LENGTH = 3600
    const val MAX_SHORT_TEXT_LENGTH = 512

    private val keyPattern = Regex("^[a-z0-9-]{1,$MAX_KEY_LENGTH}$")

    fun isValidKey(key: String) = keyPattern.matches(key)

    fun isValid(translation: FaqTranslation) =
        translation.question.length in 1..MAX_QUESTION_LENGTH &&
                translation.longText.length in 1..MAX_LONG_TEXT_LENGTH &&
                translation.shortText.length in 1..MAX_SHORT_TEXT_LENGTH
}

private const val MAX_CHOICES = OptionData.MAX_CHOICES

fun searchFaqEntries(
    entries: List<FaqEntry>,
    input: String,
    platform: FaqPlatform
): List<FaqEntry> {
    val query = input.trim().lowercase()

    return entries.asSequence()
        .filter { it.isActive(platform) }
        .filter { query in it.key || query in it.translation().question.lowercase() }
        .sortedWith(compareBy<FaqEntry> { !it.key.startsWith(query) }.thenBy { it.key })
        .take(MAX_CHOICES)
        .toList()
}
