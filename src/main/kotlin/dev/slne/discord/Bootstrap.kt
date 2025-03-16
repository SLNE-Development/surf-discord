package dev.slne.discord

import com.github.ajalt.clikt.command.parse
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.ContextCliktError
import dev.slne.discord.console.buildRootCommand
import dev.slne.discord.discord.interaction.command.DiscordCommandProcessor
import dev.slne.discord.message.RawMessages
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

private val logger = ComponentLogger.logger("Bootstrap")

@Component
class Bootstrap(
    private val jda: JDA,
    private val commandProcessor: DiscordCommandProcessor,
) {

    @PostConstruct
    fun onLoad() {
        val ms = measureTimeMillis {
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                logger.error("Uncaught exception in thread ${thread.name}", throwable)
            }

            logger.info("Loading messages...")
            RawMessages::class.java.getClassLoader()

            listenForCommands(jda, commandProcessor)
        }

        logger.info("Done and ready ({}ms)! Type 'help' for a list of commands.", ms)
    }

    @PreDestroy
    fun onDisable() {
    }
}

private fun listenForCommands(
    jda: JDA,
    commandProcessor: DiscordCommandProcessor,
) = thread {
    val root = buildRootCommand(jda, commandProcessor)

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


