package dev.slne.discord;

import dev.slne.discord.spring.processor.DiscordCommandProcessor;
import dev.slne.discord.spring.service.ticket.TicketService;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapFinalizer implements CommandLineRunner {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("BootstrapFinalizer");

  private final TicketService ticketService;
  private final DiscordCommandProcessor discordCommandProcessor;
  private final JDA jda;

  public BootstrapFinalizer(TicketService ticketService,
      DiscordCommandProcessor discordCommandProcessor, JDA jda) {
    this.ticketService = ticketService;
    this.discordCommandProcessor = discordCommandProcessor;
    this.jda = jda;
  }

  @Override
  public void run(String... args) throws Exception {
    LOGGER.info("Finalizing bootstrapping...");

    LOGGER.debug("Fetching active tickets...");
    ticketService.fetchActiveTickets();

//    LOGGER.debug("Updating commands for all guilds...");
//    for (final Guild guild : jda.getGuilds()) {
//      discordCommandProcessor.updateCommands(guild);
//    }

    LOGGER.info("Bootstrapping finalized.");
  }
}
