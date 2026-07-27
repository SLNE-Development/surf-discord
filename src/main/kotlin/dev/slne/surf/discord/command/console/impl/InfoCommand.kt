package dev.slne.surf.discord.command.console.impl

import dev.slne.surf.discord.jda
import dev.slne.surf.microservice.api.microservice.command.microserviceCommand

fun infoCommand() = microserviceCommand("info") {
    sendLine("---- Guild Information ----")
    jda.guilds.forEach {
        sendLine("Connected to guild: ${it.name} (ID: ${it.id})")
    }
}