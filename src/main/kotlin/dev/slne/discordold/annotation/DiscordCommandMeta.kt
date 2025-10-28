package dev.slne.discordold.annotation

import dev.slne.discordold.guild.permission.CommandPermission
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class DiscordCommandMeta(
    val name: String,
    val description: String,
    val permission: CommandPermission,
    val guildOnly: Boolean = true,
    val nsfw: Boolean = false,
    val ephemeral: Boolean = true,
    val sendTyping: Boolean = false
)
