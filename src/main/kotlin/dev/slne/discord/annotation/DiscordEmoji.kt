package dev.slne.discord.annotation

import dev.minn.jda.ktx.emoji.toEmoji
import dev.minn.jda.ktx.emoji.toUnicodeEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordEmoji(
    val unicode: String = "",
    val formatted: String = "",
    val custom: DiscordCustomEmoji = DiscordCustomEmoji(
        id = -1,
        name = "",
        animated = false
    )
) {
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DiscordCustomEmoji(val id: Long, val name: String, val animated: Boolean)

    object Factory {
        @JvmStatic
        fun create(discordEmoji: DiscordEmoji): Emoji? {
            val unicode = discordEmoji.unicode
            val formatted = discordEmoji.formatted
            val custom = discordEmoji.custom

            if (unicode.isNotEmpty()) {
                return unicode.toUnicodeEmoji()
            } else if (formatted.isNotEmpty()) {
                return formatted.toEmoji()
            } else {
                val id = custom.id
                val name = custom.name
                val animated = custom.animated

                if (id == -1L || name.isEmpty()) {
                    return null
                }

                return Emoji.fromCustom(name, id, animated)
            }
        }
    }
}
