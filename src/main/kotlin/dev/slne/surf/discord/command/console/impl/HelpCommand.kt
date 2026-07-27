package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.command.console.ConsoleCommand

object HelpCommand : ConsoleCommand {
    override val name = "help"

    override fun execute(args: List<String>) {
        println("Verfügbare Console Commands:")
        println("   - help: Zeigt alle verfügbaren Console Commands an")
        println("   - registercommands: Registriert alle Discord-Commands")
        println("   - unregistercommands: Entfernt alle Discord-Commands")
        println("   - createartyemojis: Erstellt die Arty Emojis neu")
        println("   - clear-whitelist-role: Entfernt alle Nutzer von der Whitelisted Rolle")
    }
}