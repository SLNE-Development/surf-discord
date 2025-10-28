package dev.slne.surf.discord.command

import dev.slne.surf.discord.logger
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class CommandRegistrar(
    private val jda: JDA,
    private val context: ApplicationContext,
    private val commandScope: CoroutineScope
) {
    private val registeredCommands = mutableMapOf<String, SlashCommand>()

    @PostConstruct
    fun init() {
        registerAllCommands()

        jda.addEventListener(object : ListenerAdapter() {
            override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                registeredCommands[event.name]?.let { command ->
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
                registerCommand(annotation.name, annotation.description, bean)
            }
        }

        if (registeredCommands.isEmpty()) {
            logger.warn("No Discord commands were found to register.")
        } else {
            logger.info("Registered ${registeredCommands.size} Discord commands.")
        }
    }

    fun registerCommand(name: String, description: String, command: SlashCommand) {
        jda.guilds.forEach {
            it.upsertCommand(name, description).queue()
        }

        logger.info("Successfully registered command '$name' for ${jda.guilds.size} guilds.")

        registeredCommands[name] = command
    }
}
