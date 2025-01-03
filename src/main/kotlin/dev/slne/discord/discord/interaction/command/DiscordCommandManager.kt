package dev.slne.discord.discord.interaction.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.commands.misc.*
import dev.slne.discord.discord.interaction.command.commands.ticket.*
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberAddCommand
import dev.slne.discord.discord.interaction.command.commands.ticket.members.TicketMemberRemoveCommand
import dev.slne.discord.discord.interaction.command.commands.whitelist.*
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.reflect.full.findAnnotation

object DiscordCommandManager {
    private val logger = ComponentLogger.logger(DiscordCommandManager::class.java)
    private val commands: Object2ObjectMap<String, DiscordCommandHolder> =
        Object2ObjectOpenHashMap()

    init {
        register(WhitelistCommand)
        register(WhitelistedCommand)
        register(WhitelistQueryCommand)
        register(WhitelistRoleRemoveCommand)
        register(WhitelistUnblockCommand)
        register(WhitelistChangeCommand)

        register(TicketButtonCommand)
        register(TicketCloseCommand)
        register(TicketDependenciesNotMetCommand)
        register(TicketFixCommand)
        register(TicketReplyDeadlineCommand)

        register(TicketMemberAddCommand)
        register(TicketMemberRemoveCommand)

        register(DontAskToAsk)
        register(HowToJoin)
        register(FAQCommand)
        register(MissingInformationCommand)

        register(RequestRollbackCommand)
    }

    private fun register(command: DiscordCommand) {
        val annotation = command::class.findAnnotation<DiscordCommandMeta>()
            ?: error("Command $command does not have a DiscordCommandMeta annotation")
        check(annotation.name !in commands) { "Duplicate command handler id ${annotation.name}" }

        val holder = DiscordCommandHolder(annotation, command)
        commands[annotation.name] = holder

        logger.info(
            "Found command ${command.javaClass.simpleName} with name ${annotation.name}"
        )
    }

    fun getCommand(name: String): DiscordCommandHolder? = commands[name]

    suspend fun updateCommands(guild: Guild) {
        logger.info("Starting to update commands for guild ${guild.name} (${guild.id})")

        val commandData = ObjectArrayList<CommandData>(commands.values.size)

        for (commandHolder in commands.values) {
            commandData.add(
                Commands.slash(commandHolder.meta.name, commandHolder.meta.description)
                    .setGuildOnly(commandHolder.meta.guildOnly)
                    .setNSFW(commandHolder.meta.nsfw)
                    .addSubcommands(commandHolder.command.subCommands)
                    .addOptions(commandHolder.command.options)
//                    .setDefaultPermissions(commandHolder.command().getDefaultMemberPermissions()) // TODO: 24.08.2024 10:11 - why not in use?
            )
        }


        guild.updateCommands().await()

        val updatedCommandNames =
            guild.updateCommands().addCommands(commandData).await().map { it.name }

        logger.info(
            "Updated ${updatedCommandNames.size} commands for guild ${guild.name} (${guild.id}) with names: $updatedCommandNames"
        )
    }
}

data class DiscordCommandHolder(val meta: DiscordCommandMeta, val command: DiscordCommand)
