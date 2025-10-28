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
                val cmdName = parts[0]
                val args = parts.drop(1)
                val cmd = commands[cmdName]
                if (cmd != null) {
                    cmd.execute(args)
                } else {
                    println("Unknown command: $cmdName. Type 'help' for commands.")
                }
            }
        }.start()
    }
}
