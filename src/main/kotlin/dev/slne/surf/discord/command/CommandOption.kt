package dev.slne.surf.discord.command

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

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

fun CommandOption.toOptionData(): OptionData {
    val data = OptionData(
        this.type.jdaType,
        this.name,
        this.description,
        this.required
    )

    data.isAutoComplete = autocomplete

    this.choices.forEach { choice ->
        data.addChoice(choice.name, choice.value)
    }

    return data
}

enum class CommandOptionType(
    val jdaType: OptionType
) {
    STRING(OptionType.STRING),
    INTEGER(OptionType.INTEGER),
    BOOLEAN(OptionType.BOOLEAN),
    USER(OptionType.USER),
    CHANNEL(OptionType.CHANNEL),
    ROLE(OptionType.ROLE),
    MENTIONABLE(OptionType.MENTIONABLE),
    NUMBER(OptionType.NUMBER)
}

annotation class CommandChoice(
    val name: String,
    val value: String
)