package dev.slne.discord.annotation

import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordButton(
    val id: String,
    val label: String,
    val style: ButtonStyle,
    val emoji: DiscordEmoji = DiscordEmoji()
)

fun DiscordButton.toJdaButton() = Button.of(style, id, label, DiscordEmoji.Factory.create(emoji))
