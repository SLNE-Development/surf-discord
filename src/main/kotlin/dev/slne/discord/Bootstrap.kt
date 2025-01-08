package dev.slne.discord

import com.github.ajalt.clikt.command.parse
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.core.ContextCliktError
import dev.slne.discord.console.buildRootCommand
import dev.slne.discord.message.RawMessages
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Component
import kotlin.concurrent.thread

private val logger = ComponentLogger.logger("Bootstrap")

@Component
class Bootstrap {

    private var startTimestamp: Long? = null

    @PostConstruct
    fun onLoad() {
        logger.info("Loading messages...")
        RawMessages::class.java.getClassLoader()

        listenForCommands()

        startTimestamp = System.currentTimeMillis()


        logger.info(
            "Done ({}ms)! Type 'help' for a list of commands.",
            System.currentTimeMillis() - startTimestamp!!
        )
    }

    @PreDestroy
    fun onDisable() {
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


