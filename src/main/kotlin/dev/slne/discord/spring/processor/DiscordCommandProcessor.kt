package dev.slne.discord.spring.processor

import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import lombok.experimental.Delegate
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
class DiscordCommandProcessor : BeanPostProcessor {
    private val commands: Object2ObjectMap<String, DiscordCommandHolder> =
        Object2ObjectOpenHashMap()

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation: DiscordCommandMeta = AnnotationUtils.findAnnotation<DiscordCommandMeta>(
            bean.javaClass,
            DiscordCommandMeta::class.java
        )

        if (annotation != null) {
            if (bean !is DiscordCommand) {
                throw BeanCreationException(
                    ("Bean " + beanName
                            + " is annotated with @DiscordCommandMeta but does not extend DiscordCommand.")
                )
            }

            val holder = DiscordCommandHolder(annotation, bean)
            commands[annotation.name] = holder

            LOGGER.info("Found command {} with name {}", beanName, annotation.name)
        }

        return bean
    }

    fun getCommand(name: String): Optional<DiscordCommandHolder> {
        return Optional.ofNullable(
            commands[name]
        )
    }

    @Async
    fun updateCommands(guild: Guild) {
        LOGGER.info("Starting to update commands for guild {} ({})", guild.name, guild.id)

        val commandDatas: ObjectList<CommandData> =
            ObjectArrayList<CommandData>(commands.values.size)

        for (commandHolder in commands.values) {
            commandDatas.add(
                Commands.slash(commandHolder.name(), commandHolder.description())
                    .setGuildOnly(commandHolder.guildOnly())
                    .setNSFW(commandHolder.nsfw())
                    .addSubcommands(commandHolder.command.getSubCommands())
                    .addOptions(commandHolder.command.getOptions()) //          .setDefaultPermissions(commandHolder.command().getDefaultMemberPermissions()) // TODO: 24.08.2024 10:11 - why not in use?
            )
        }

        val updatedCommandNames = guild.updateCommands().addCommands(commandDatas)
            .complete()
            .stream()
            .map { obj: Command -> obj.name }
            .toList()

        LOGGER.info(
            "Updated {} commands for guild {} ({}) with names: {}",
            updatedCommandNames.size,
            guild.name,
            guild.id,
            updatedCommandNames
        )
    }

    class DiscordCommandHolder(@Delegate meta: DiscordCommandMeta, command: DiscordCommand) {
        @Delegate
        val meta: DiscordCommandMeta = meta
        val command: DiscordCommand = command
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("DiscordCommandProcessor")
    }
}
