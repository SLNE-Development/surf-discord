package dev.slne.discord.config.ticket

import dev.slne.discord.ticket.TicketType
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class TicketTypeConfig {
    var enabled = false
        private set

    var shouldPrintWlQuery = false
        private set

    lateinit var openingMessages: List<String>
}

fun getTicketTypeConfig(ticketType: String) = ticketConfig.ticketTypes[ticketType]

fun TicketType.getConfig() = getTicketTypeConfig(getConfigName())
