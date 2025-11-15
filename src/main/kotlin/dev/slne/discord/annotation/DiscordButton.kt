package dev.slne.discord.annotation

import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class DiscordButton(
    val id: String,
    val label: String,
    val style: ButtonStyle,
    val emoji: DiscordEmoji = DiscordEmoji()
)

fun DiscordButton.toJdaButton() = Button.of(style, id, label, DiscordEmoji.Factory.create(emoji))
