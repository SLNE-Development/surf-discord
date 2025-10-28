package dev.slne.discordold.console.commands

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.theme
import kotlin.system.exitProcess

object ExitCommand : SuspendingCliktCommand("exit") {
    override suspend fun run() {
        currentContext.theme.info("Exiting...")
        exitProcess(0)
    }
}