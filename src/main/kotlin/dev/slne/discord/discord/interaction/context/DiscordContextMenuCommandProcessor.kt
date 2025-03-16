package dev.slne.discord.discord.interaction.context

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordContextMenuCommandMeta
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
class DiscordContextMenuCommandProcessor : BeanPostProcessor {
    private val logger = ComponentLogger.logger()
    private val commands = mutableObject2ObjectMapOf<String, DiscordContextMenuCommandHolder>()

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation = bean.ultimateTargetClass().findAnnotation<DiscordContextMenuCommandMeta>()

        if (bean is DiscordContextMenuCommand<*> && annotation != null) {
            register(bean, annotation)
        }

        return bean
    }

    private fun register(
        command: DiscordContextMenuCommand<*>,
        annotation: DiscordContextMenuCommandMeta
    ) {
        check(annotation.name !in commands) { "Duplicate command handler id ${annotation.name}" }

        val holder = DiscordContextMenuCommandHolder(annotation, command)
        commands[annotation.name] = holder

        logger.info("Found command ${command.javaClass.simpleName} with name ${annotation.name}")
    }

    fun getContextMenuCommand(name: String): DiscordContextMenuCommandHolder? = commands[name]
    operator fun get(name: String): DiscordContextMenuCommandHolder? = getContextMenuCommand(name)

    suspend fun updateCommands(guild: Guild) {
        logger.info("Starting to update context menu commands for guild ${guild.name} (${guild.id})")
        val commandData = mutableObjectListOf<CommandData>(commands.values.size)

        for ((meta, command) in commands.values) {
            commandData.add(
                Commands.context(command.type.jdaMap, meta.name)
                    .setGuildOnly(meta.guildOnly)
                    .setNSFW(meta.nsfw)
            )
        }

        guild.updateCommands().await()

        val updatedCommandNames =
            guild.updateCommands().addCommands(commandData).await().map { it.name }

        logger.info("Updated ${updatedCommandNames.size} context menu commands for guild ${guild.name} (${guild.id}) with names: $updatedCommandNames")
    }
}

typealias DiscordContextMenuCommandHolder = Pair<DiscordContextMenuCommandMeta, DiscordContextMenuCommand<*>>