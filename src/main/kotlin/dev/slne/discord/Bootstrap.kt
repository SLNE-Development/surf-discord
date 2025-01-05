package dev.slne.discord

import com.github.ajalt.clikt.command.parse
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.ContextCliktError
import dev.slne.discord.console.buildRootCommand
import dev.slne.discord.message.RawMessages
import dev.slne.discord.persistence.service.ticket.TicketService
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import kotlin.concurrent.thread

private val logger = ComponentLogger.logger("Bootstrap")

fun main(args: Array<String>) {
    runBlocking {
        val bootstrap = Bootstrap()

        logger.info("Loading messages...")
        RawMessages::class.java.getClassLoader()

        bootstrap.onLoad()
        bootstrap.onEnable()
    }

    listenForCommands()
}


class Bootstrap {

    private var startTimestamp: Long? = null

    suspend fun onLoad() {
        Runtime.getRuntime().addShutdownHook(thread(start = false) { onDisable() })
        startTimestamp = System.currentTimeMillis()

        DiscordBot.onLoad()
        TicketService.fetchActiveTickets()
    }

    fun onEnable() {
        DiscordBot.onEnable()
        logger.info(
            "Done ({}ms)! Type 'help' for a list of commands.",
            System.currentTimeMillis() - startTimestamp!!
        )
    }

    fun onDisable() {
        DiscordBot.onDisable()
    }
}

private fun listenForCommands() = thread {
    val root = buildRootCommand()

    print("> ")
    while (true) {
        val input = readlnOrNull() ?: continue

        try {
            if (input == "help") {
                root.echoFormattedHelp()
            } else {
                runBlocking { root.parse(input.split(" ")) }
            }
        } catch (error: CliktError) {
            if (error is ContextCliktError) {
                error.context?.command?.echoFormattedHelp(error)
            } else {
                root.echoFormattedHelp(error)
            }

        } catch (exception: Exception) {
            logger.error("Failed to parse command.", exception)
        }

        print("> ")
    }
}


