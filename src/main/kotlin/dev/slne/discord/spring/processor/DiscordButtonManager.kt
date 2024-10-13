package dev.slne.discord.spring.processor

import dev.slne.discord.annotation.DiscordButton
import dev.slne.discord.discord.interaction.button.DiscordButtonHandler
import dev.slne.discord.discord.interaction.button.buttons.OpenTicketButton
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.reflect.full.findAnnotation


object DiscordButtonManager {
    private val logger = ComponentLogger.logger()
    private val handlers = Object2ObjectOpenHashMap<String, DiscordButtonHandlerHolder>()

    init {
        register(OpenTicketButton)
    }

    private fun register(handler: DiscordButtonHandler) {
        val annotation = handler::class.findAnnotation<DiscordButton>()
            ?: error("Handler $handler does not have a DiscordButton annotation")
        check(annotation.id !in handlers) { "Duplicate button handler id ${annotation.id}" }

        val holder = DiscordButtonHandlerHolder(annotation, handler)
        handlers[annotation.id] = holder

        logger.info("Found button handler {} with id {}", handler::class.simpleName, annotation.id)
    }

    fun getHandler(id: String) = handlers[id]
    operator fun get(id: String) = getHandler(id)
}

data class DiscordButtonHandlerHolder(
    val info: DiscordButton,
    val handler: DiscordButtonHandler
)
