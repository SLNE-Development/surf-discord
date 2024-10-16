package dev.slne.discord

import dev.slne.discord.message.RawMessages
import dev.slne.discord.persistence.service.ticket.TicketService
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.system.measureTimeMillis

private val logger = ComponentLogger.logger("Bootstrap")

fun main(args: Array<String>) = runBlocking {
    val bootstrap = Bootstrap()

    logger.info("Loading messages...")
    RawMessages::class.java.getClassLoader()

    bootstrap.onLoad()
    bootstrap.onEnable()
}


class Bootstrap {
    suspend fun onLoad() {
        val duration = measureTimeMillis {
            DiscordBot.onLoad()
            TicketService.fetchActiveTickets()
        }

        logger.info("Done ({}ms)! Type 'help' for a list of commands.", duration)
    }

    fun onEnable() {
        DiscordBot.onEnable()
    }

    fun onDisable() {
        DiscordBot.onDisable()
    }
}


