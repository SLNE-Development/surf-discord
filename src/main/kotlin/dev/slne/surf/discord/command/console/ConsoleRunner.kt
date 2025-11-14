package dev.slne.surf.discord.command.console

import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class ConsoleRunner(
    private val context: ApplicationContext
) {
    @PostConstruct
    fun init() {
        val commands = context.getBeansOfType(ConsoleCommand::class.java)
            .values
            .associateBy { it.name }

        Thread {
            val reader = System.`in`.bufferedReader()

            while (true) {
                val line = reader.readLine() ?: break
                val parts = line.trim().split("\\s+".toRegex())

                if (parts.isEmpty()) continue

                val commandName = parts[0]
                val arguments = parts.drop(1)
                val command = commands[commandName]

                if (command != null) {
                    command.execute(arguments)
                } else {
                    println("Unknown command: $commandName. Type 'help' for commands.")
                }
            }
        }.start()
    }
}
