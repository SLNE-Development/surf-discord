package dev.slne.discord.config.ticket

import dev.slne.discord.config.botConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

val ticketConfig: TicketConfig
    get() = botConfig.ticketConfig

@ConfigSerializable
class TicketConfig {

    var enabled = false
        private set

    lateinit var ticketTypes: Map<String, TicketTypeConfig>
}
