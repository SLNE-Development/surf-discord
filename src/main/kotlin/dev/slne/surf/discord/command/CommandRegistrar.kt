package dev.slne.surf.discord.command

import dev.slne.surf.discord.logger
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
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
        jda.upsertCommand(name, description).queue()
        jda.addEventListener(object : ListenerAdapter() {
            override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                if (event.name == name) {
                    commandScope.launch {
                        command.execute(event)
                    }
                }
            }
        })

        registeredCommands[name] = command
    }

    fun getRegisteredCommands(): Object2ObjectMap<String, SlashCommand> =
        Object2ObjectOpenHashMap(registeredCommands)
}
