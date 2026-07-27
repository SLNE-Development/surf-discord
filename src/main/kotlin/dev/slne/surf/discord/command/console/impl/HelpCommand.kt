package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.microservice.api.microservice.command.microserviceCommand

fun helpCommand() = microserviceCommand("help") {
    sendLine("Verfügbare Console Commands:")
    sendLine("  — help: Zeigt alle verfügbaren Console Commands an")
    sendLine("  — registercommands: Registriert alle Discord-Commands")
    sendLine("  — unregistercommands: Entfernt alle Discord-Commands")
    sendLine("  — createartyemojis: Erstellt die Arty Emojis neu")
    sendLine("  — clear-whitelist-role: Entfernt alle Nutzer von der Whitelisted Rolle")
}