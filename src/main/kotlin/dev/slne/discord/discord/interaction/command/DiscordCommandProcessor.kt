package dev.slne.discord.discord.interaction.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.annotation.DiscordContextMenuCommandMeta
import dev.slne.discord.discord.interaction.command.context.DiscordContextMenuCommand
import dev.slne.discord.util.mutableObject2ObjectMapOf
import dev.slne.discord.util.mutableObjectListOf
import dev.slne.discord.util.ultimateTargetClass
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import kotlin.reflect.full.findAnnotation

@Component
class DiscordCommandProcessor : BeanPostProcessor {
    private val logger = ComponentLogger.logger()

    private val commands = mutableObject2ObjectMapOf<String, DiscordCommandHolder>()
    private val contextMenuCommands =
        mutableObject2ObjectMapOf<String, DiscordContextMenuCommandHolder>()

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val commandMetaAnnotation = bean.ultimateTargetClass().findAnnotation<DiscordCommandMeta>()
        val contextMenuCommandMetaAnnotation = bean.ultimateTargetClass()
            .findAnnotation<DiscordContextMenuCommandMeta>()

        if (bean is DiscordCommand && commandMetaAnnotation != null) {
            register(bean, commandMetaAnnotation)
        }

        if (bean is DiscordContextMenuCommand<*> && contextMenuCommandMetaAnnotation != null) {
            register(bean, contextMenuCommandMetaAnnotation)
        }

        return bean
    }

    private fun register(command: DiscordCommand, annotation: DiscordCommandMeta) {
        check(annotation.name !in commands) { "Duplicate command handler id ${annotation.name}" }

        val holder = DiscordCommandHolder(annotation, command)
        commands[annotation.name] = holder

        logger.debug("Found command ${command.javaClass.simpleName} with name ${annotation.name}")
    }

    private fun register(
        command: DiscordContextMenuCommand<*>,
        annotation: DiscordContextMenuCommandMeta
    ) {
        check(annotation.name !in contextMenuCommands) { "Duplicate command handler id ${annotation.name}" }

        val holder = DiscordContextMenuCommandHolder(annotation, command)
        contextMenuCommands[annotation.name] = holder

        logger.debug("Found context menu command ${command.javaClass.simpleName} with name ${annotation.name}")
    }

    fun getCommand(name: String): DiscordCommandHolder? = commands[name]
    fun getContextMenuCommand(name: String): DiscordContextMenuCommandHolder? =
        contextMenuCommands[name]

    suspend fun updateCommands(guild: Guild) {
        logger.info("Starting to update commands for guild ${guild.name} (${guild.id})")
        val commandData = mutableObjectListOf<CommandData>(commands.values.size)

        for ((meta, command) in commands.values) {
            commandData.add(
                Commands.slash(meta.name, meta.description)
                    .setGuildOnly(meta.guildOnly)
                    .setNSFW(meta.nsfw)
                    .addSubcommands(command.subCommands)
                    .addOptions(command.options)
            )
        }

        for ((meta, command) in contextMenuCommands.values) {
            commandData.add(
                Commands.context(command.type.jdaMap, meta.name)
                    .setGuildOnly(meta.guildOnly)
                    .setNSFW(meta.nsfw)
            )
        }

        guild.updateCommands().await()

        val updatedCommandNames =
            guild.updateCommands().addCommands(commandData).await().map { it.name }

        logger.info("Updated ${updatedCommandNames.size} commands for guild ${guild.name} (${guild.id}) with names: $updatedCommandNames")
    }
}

typealias DiscordCommandHolder = Pair<DiscordCommandMeta, DiscordCommand>
typealias DiscordContextMenuCommandHolder = Pair<DiscordContextMenuCommandMeta, DiscordContextMenuCommand<*>>
