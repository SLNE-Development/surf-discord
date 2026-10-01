package dev.slne.surf.discord.command

import dev.slne.surf.discord.DiscordBot.jda
import dev.slne.surf.discord.contextmenu.ContextCommandRegistrar
import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.faq.command.FaqCommand
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.command.*
import dev.slne.surf.discord.ticket.command.context.*
import dev.slne.surf.discord.ticket.command.whitelist.ViewWhitelistCommand
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands

object CommandRegistrar {
    private val discordCommands = listOf(
        CloseTicketCommand, DeadlineNotifyCommand,
        FixTicketsCommand, PrintTicketButtonsCommand, TicketAddEntityCommand,
        TicketRemoveUserCommand, TicketReplyDeadlineCommand, FaqCommand, MyTicketsCommand,
        RequestRefundCommand, RequestFullRollbackCommand, RequestTimedRollbackCommand,
        RequestRadiusRollbackCommand, ViewWhitelistCommand
    )
    private val commandNames = discordCommands.map {
        it.javaClass.getAnnotation(DiscordCommand::class.java) to it
    }.associateBy { it.first.name }

    fun init() {
        registerAllCommands()

        jda.addEventListener(object : ListenerAdapter() {
            override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
                commandNames[event.name]?.let { (_, command) ->
                    discordScope.launch { command.execute(event) }
                    logger.info("${event.user.name} executed discord command '${event.commandString}'")
                }
            }

            override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
                commandNames[event.name]?.let { (_, command) ->
                    discordScope.launch { command.autocomplete(event) }
                }
            }
        })
    }

    fun registerAllCommands() {
        val commands = commandNames.values.map { (annotation, _) ->
            Commands.slash(annotation.name, annotation.description)
                .addOptions(annotation.options.map { it.toOptionData() })
        } + ContextCommandRegistrar.commandData()

        jda.guilds.forEach { guild ->
            guild.updateCommands().addCommands(commands).queue()

            logger.info("Successfully registered ${commands.size} commands for guild '${guild.name}'")
        }

        if (commandNames.isEmpty()) {
            logger.warn("No Discord commands were found to register.")
        } else {
            logger.info("Registered ${commandNames.size} Discord commands.")
        }
    }

    fun unregisterAllCommands() {
        jda.guilds.forEach { guild ->
            guild.updateCommands().queue()
        }

        logger.info("Unregistered all Discord commands.")
    }
}
