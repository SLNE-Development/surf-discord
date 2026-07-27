package dev.slne.surf.discord.command

import dev.slne.surf.discord.DiscordBot.jda
import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.faq.command.FaqCommand
import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.command.*
import dev.slne.surf.discord.ticket.command.context.*
import dev.slne.surf.discord.ticket.command.whitelist.ViewWhitelistCommand
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
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
        })
    }

    fun registerAllCommands() {
        jda.guilds.forEach { guild ->
            commandNames.forEach { _, (annotation, _) ->
                registerCommand(annotation.name, annotation.description, guild, annotation.options)
            }
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

    fun registerCommand(
        name: String,
        description: String,
        guild: Guild,
        options: Array<CommandOption> = emptyArray()
    ) {
        val commandData = Commands.slash(name, description).addOptions(options.map {
            it.toOptionData()
        })

        guild.upsertCommand(commandData).queue()

        logger.info("Successfully registered command '$name' for guild '${guild.name}'")
    }
}
