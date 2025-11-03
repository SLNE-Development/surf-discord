package dev.slne.surf.discord.util.registry

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.util.mutableObjectListOf
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.config.BeanPostProcessor
import kotlin.reflect.full.findAnnotation

class ListenerAutoRegistry2 : BeanPostProcessor {
    private val listeners = mutableObjectListOf<ListenerAdapter>()

    override fun postProcessAfterInitialization(
        bean: Any,
        beanName: String
    ): Any {
        if (bean !is ListenerAdapter) {
            return bean
        }

        val annotation = bean::class.findAnnotation<DiscordCommand>()

        if (annotation != null) {
            listeners.add(bean)
        }

        return bean
    }
}