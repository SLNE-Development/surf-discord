package dev.slne.discord.annotation

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
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
