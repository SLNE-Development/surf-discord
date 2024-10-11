package dev.slne.discord.annotation

import dev.slne.discord.guild.permission.CommandPermission

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordCommandMeta(
    val name: String,
    val description: String,
    val permission: CommandPermission,
    val guildOnly: Boolean = true,
    val nsfw: Boolean = false
)
