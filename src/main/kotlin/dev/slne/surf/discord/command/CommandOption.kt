package dev.slne.surf.discord.command

@Target()
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandOption(
    val name: String,
    val description: String,
    val type: CommandOptionType = CommandOptionType.STRING,
    val required: Boolean = false,
    val autocomplete: Boolean = false,
    val choices: Array<CommandChoice> = []
)

enum class CommandOptionType {
    STRING, INTEGER, BOOLEAN, USER, CHANNEL, ROLE, MENTIONABLE, NUMBER
}

annotation class CommandChoice(
    val name: String,
    val value: String
)