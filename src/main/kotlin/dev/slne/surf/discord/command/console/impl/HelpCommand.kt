package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.console.ConsoleCommand
import org.springframework.stereotype.Component

@Component
class HelpCommand : ConsoleCommand {
    override val name = "help"

    override fun execute(args: List<String>) {
        println("Verfügbare Console Commands:")
        println("   - help: Zeigt alle verfügbaren Console Commands an")
        println("   - registercommands: Registriert alle Discord-Commands")
        println("   - createartyemojis: Erstellt die Arty Emojis neu")
    }
}