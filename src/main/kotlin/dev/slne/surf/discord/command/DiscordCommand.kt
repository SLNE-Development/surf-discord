package dev.slne.surf.discord.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordCommand(
    val name: String,
    val description: String,
    val options: Array<CommandOption> = []
)
