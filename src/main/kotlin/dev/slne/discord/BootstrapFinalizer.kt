package dev.slne.discord

import dev.slne.discord.spring.processor.DiscordCommandProcessor
import dev.slne.discord.spring.service.ticket.TicketService
import net.dv8tion.jda.api.JDA
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class BootstrapFinalizer(
    private val ticketService: TicketService,
    private val discordCommandProcessor: DiscordCommandProcessor,
    private val jda: JDA
) : CommandLineRunner {

    private val logger = ComponentLogger.logger("BootstrapFinalizer")

    override fun run(vararg args: String) {
        logger.info("Finalizing bootstrapping...")

        logger.debug("Fetching active tickets...")
        ticketService.fetchActiveTickets()

        //    LOGGER.debug("Updating commands for all guilds...");
//    for (final Guild guild : jda.getGuilds()) {
//      discordCommandProcessor.updateCommands(guild);
//    }
        logger.info("Bootstrapping finalized.")
    }
}
