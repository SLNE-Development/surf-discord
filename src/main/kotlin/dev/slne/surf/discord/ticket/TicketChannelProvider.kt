package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.logger
import net.dv8tion.jda.api.JDA
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class TicketChannelProvider(
    private val jda: JDA
) {
    @Bean
    fun ticketChannel() = jda.getTextChannelById(botConfig.ticketChannel) ?: run {
        logger.error("Ticket channel with ID ${botConfig.ticketChannel} not found!")
        null
    }
}