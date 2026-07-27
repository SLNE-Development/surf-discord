package dev.slne.surf.discord.contextmenu

import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.command.context.ViewWhitelistInformationContextCommand
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands

object ContextCommandRegistrar : ListenerAdapter() {
    private val _userCommands = mutableMapOf<String, UserContextCommand>()

    private val contextCommands = listOf(
        ViewWhitelistInformationContextCommand,
    )

    fun registerAll() {
        contextCommands.forEach { bean ->
            val annotation = bean::class.java.getAnnotation(DiscordContextCommand::class.java)

            when (annotation.type) {
                ContextCommandType.USER -> {
                    jda.guilds.forEach {
                        it.upsertCommand(Commands.user(annotation.name)).queue()
                    }

                    _userCommands[annotation.name] = bean
                    logger.info("Registered USER context command '${annotation.name}'.")
                }
            }
        }
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        val command = _userCommands[event.name] ?: return
        discordScope.launch { command.execute(event) }
    }
}
