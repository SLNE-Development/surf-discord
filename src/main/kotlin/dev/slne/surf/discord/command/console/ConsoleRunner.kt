package dev.slne.surf.discord.command.console

import dev.slne.surf.discord.command.console.impl.*

object ConsoleRunner {
    private val consoleCommands = listOf(
        EmojiCreateCommand, HelpCommand, InfoCommand,
        RegisterCommand, UnregisterCommandsCommand
    )

    fun init() {
        val commands = consoleCommands
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
