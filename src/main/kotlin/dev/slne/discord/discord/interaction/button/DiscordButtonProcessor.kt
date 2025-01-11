package dev.slne.discord.discord.interaction.button

import dev.slne.discord.annotation.DiscordButton
import dev.slne.discord.util.findAnnotation
import dev.slne.discord.util.mutableObject2ObjectMapOf
import dev.slne.discord.util.ultimateTargetClass
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
class DiscordButtonProcessor : BeanPostProcessor {
    private val logger = ComponentLogger.logger()
    private val handlers = mutableObject2ObjectMapOf<String, DiscordButtonHandlerHolder>()

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val annotation = bean.ultimateTargetClass().findAnnotation<DiscordButton>()

        if (bean is DiscordButtonHandler && annotation != null) {
            register(bean, annotation)
        }

        return bean
    }

    private fun register(handler: DiscordButtonHandler, annotation: DiscordButton) {
        check(annotation.id !in handlers) { "Duplicate button handler id ${annotation.id}" }

        val holder = DiscordButtonHandlerHolder(annotation, handler)
        handlers[annotation.id] = holder

        logger.info("Found button handler {} with id {}", handler::class.simpleName, annotation.id)
    }

    fun getHandler(id: String) = handlers[id]
    operator fun get(id: String) = getHandler(id)
}

typealias DiscordButtonHandlerHolder = Pair<DiscordButton, DiscordButtonHandler>
