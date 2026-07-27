package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommand
import dev.slne.surf.microservice.api.microservice.command.MicroserviceCommandContext

object HelpCommand : MicroserviceCommand("help") {
    override suspend fun MicroserviceCommandContext.execute(
        args: List<String>
    ) {
        println("Verfügbare Console Commands:")
        println("   - help: Zeigt alle verfügbaren Console Commands an")
        println("   - registercommands: Registriert alle Discord-Commands")
        println("   - unregistercommands: Entfernt alle Discord-Commands")
        println("   - createartyemojis: Erstellt die Arty Emojis neu")
        println("   - clear-whitelist-role: Entfernt alle Nutzer von der Whitelisted Rolle")
    }
}