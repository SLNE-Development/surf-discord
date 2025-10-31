package dev.slne.surf.discord.command

import dev.slne.surf.discord.logger
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.interactions.commands.OptionType as DiscordOptionType

@Component
class CommandRegistrar(
    private val jda: JDA,
    private val context: ApplicationContext,
    private val commandScope: CoroutineScope
) {
    private val _commands = mutableMapOf<String, SlashCommand>()

    @PostConstruct
    fun init() {
        registerAllCommands()

        jda.addEventListener(object : ListenerAdapter() {
            override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                _commands[event.name]?.let { command ->
                    commandScope.launch { command.execute(event) }
                }
            }
        })
    }

    fun registerAllCommands() {
        val commands = context.getBeansWithAnnotation(DiscordCommand::class.java)

        commands.forEach { (_, bean) ->
            val annotation = bean::class.java.getAnnotation(DiscordCommand::class.java)
            if (bean is SlashCommand) {
                registerCommand(annotation.name, annotation.description, bean, annotation.options)
            }
        }

        if (_commands.isEmpty()) {
            logger.warn("No Discord commands were found to register.")
        } else {
            logger.info("Registered ${_commands.size} Discord commands.")
        }
    }

    fun registerCommand(
        name: String,
        description: String,
        command: SlashCommand,
        options: Array<CommandOption> = emptyArray()
    ) {
        jda.guilds.forEach { guild ->
            val commandData = Commands.slash(name, description)

            options.forEach { opt ->
                val optData = when (opt.type) {
                    CommandOptionType.STRING -> OptionData(
                        DiscordOptionType.STRING,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.INTEGER -> OptionData(
                        DiscordOptionType.INTEGER,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.BOOLEAN -> OptionData(
                        DiscordOptionType.BOOLEAN,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.USER -> OptionData(
                        DiscordOptionType.USER,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.CHANNEL -> OptionData(
                        DiscordOptionType.CHANNEL,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.ROLE -> OptionData(
                        DiscordOptionType.ROLE,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.MENTIONABLE -> OptionData(
                        DiscordOptionType.MENTIONABLE,
                        opt.name,
                        opt.description,
                        opt.required
                    )

                    CommandOptionType.NUMBER -> OptionData(
                        DiscordOptionType.NUMBER,
                        opt.name,
                        opt.description,
                        opt.required
                    )
                }

                opt.choices.forEach { choice ->
                    optData.addChoice(choice.name, choice.value)
                }

                optData.setAutoComplete(opt.autocomplete)
                commandData.addOptions(optData)
            }

            guild.upsertCommand(commandData).queue()
        }

        _commands[name] = command
        logger.info("Successfully registered command '$name' for ${jda.guilds.size} guilds.")
    }
}
