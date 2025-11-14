package dev.slne.surf.discord.contextmenu

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordContextCommand(
    val name: String,
    val type: ContextCommandType
)
