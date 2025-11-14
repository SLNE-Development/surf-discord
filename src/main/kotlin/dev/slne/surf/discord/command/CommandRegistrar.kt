package dev.slne.surf.discord.command

import dev.slne.surf.discord.logger
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class CommandRegistrar(
    private val jda: JDA,
    private val commandScope: CoroutineScope,
    commandProvider: ObjectProvider<SlashCommand>
) {
    private val commands = commandProvider.map {
        it.javaClass.getAnnotation(DiscordCommand::class.java) to it
    }.associateBy { it.first.name }

    @PostConstruct
    fun init() {
        registerAllCommands()

        jda.addEventListener(object : ListenerAdapter() {
            override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                commands[event.name]?.let { (_, command) ->
                    commandScope.launch { command.execute(event) }
                }
            }
        })
    }

    fun registerAllCommands() {
        commands.forEach { _, (annotation, _) ->
            registerCommand(annotation.name, annotation.description, annotation.options)
        }

        if (commands.isEmpty()) {
            logger.warn("No Discord commands were found to register.")
        } else {
            logger.info("Registered ${commands.size} Discord commands.")
        }
    }

    fun registerCommand(
        name: String,
        description: String,
        options: Array<CommandOption> = emptyArray()
    ) {
        jda.guilds.forEach { guild ->
            val commandData = Commands.slash(name, description).addOptions(options.map {
                it.toOptionData()
            })

            guild.upsertCommand(commandData).queue() // TODO: Fix this to send all commands at once
        }

        logger.info("Successfully registered command '$name' for ${jda.guilds.size} guilds.")
    }
}
