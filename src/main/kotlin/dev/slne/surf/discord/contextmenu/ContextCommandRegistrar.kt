package dev.slne.surf.discord.contextmenu

import dev.slne.surf.discord.logger
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class ContextCommandRegistrar(
    private val jda: JDA,
    private val context: ApplicationContext,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {
    private val _userCommands = mutableMapOf<String, UserContextCommand>()
    private val _messageCommands = mutableMapOf<String, MessageContextCommand>()

    @PostConstruct
    fun init() {
        registerAll()
    }

    private fun registerAll() {
        val beans = context.getBeansWithAnnotation(DiscordContextCommand::class.java)

        if (beans.isEmpty()) {
            logger.warn("No Discord context commands found.")
            return
        }

        beans.forEach { (_, bean) ->
            val annotation = bean::class.java.getAnnotation(DiscordContextCommand::class.java)

            when (annotation.type) {
                ContextCommandType.USER -> {
                    if (bean is UserContextCommand) {
                        jda.guilds.forEach {
                            it.upsertCommand(Commands.user(annotation.name)).queue()
                        }

                        _userCommands[annotation.name] = bean
                        logger.info("Registered USER context command '${annotation.name}'.")
                    } else {
                        logger.warn("Bean '${bean::class.simpleName}' is annotated as USER command but does not implement UserContextCommand!")
                    }
                }

                ContextCommandType.MESSAGE -> {
                    if (bean is MessageContextCommand) {
                        jda.guilds.forEach {
                            it.upsertCommand(Commands.message(annotation.name)).queue()
                        }

                        _messageCommands[annotation.name] = bean
                        logger.info("Registered MESSAGE context command '${annotation.name}'.")
                    } else {
                        logger.warn("Bean '${bean::class.simpleName}' is annotated as MESSAGE command but does not implement MessageContextCommand!")
                    }
                }
            }
        }
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        val command = _userCommands[event.name] ?: return
        discordScope.launch { command.execute(event) }
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        val command = _messageCommands[event.name] ?: return
        discordScope.launch { command.execute(event) }
    }
}
