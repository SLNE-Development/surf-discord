package dev.slne.discord.discord.interaction.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.util.mutableObjectListOf
import dev.slne.discord.util.object2ObjectMapOf
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
    private val commands = object2ObjectMapOf<String, DiscordCommandHolder>()

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation = bean.ultimateTargetClass().findAnnotation<DiscordCommandMeta>()

        if (bean is DiscordCommand && annotation != null) {
            register(bean, annotation)
        }

        return bean
    }

    private fun register(command: DiscordCommand, annotation: DiscordCommandMeta) {
        check(annotation.name !in commands) { "Duplicate command handler id ${annotation.name}" }

        val holder = DiscordCommandHolder(annotation, command)
        commands[annotation.name] = holder

        logger.info("Found command ${command.javaClass.simpleName} with name ${annotation.name}")
    }

    fun getCommand(name: String): DiscordCommandHolder? = commands[name]
    operator fun get(name: String): DiscordCommandHolder? = getCommand(name)

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

        guild.updateCommands().await()

        val updatedCommandNames =
            guild.updateCommands().addCommands(commandData).await().map { it.name }

        logger.info("Updated ${updatedCommandNames.size} commands for guild ${guild.name} (${guild.id}) with names: $updatedCommandNames")
    }
}

typealias DiscordCommandHolder = Pair<DiscordCommandMeta, DiscordCommand>
