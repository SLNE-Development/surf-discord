package dev.slne.discord.ticket.message;

import dev.slne.discord.Launcher;
import dev.slne.discord.ticket.Ticket;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.data.util.Lazy;

/**
 * The type Ticket message service.
 */
public class TicketMessageService {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketMessageService");
  public final static TicketMessageService INSTANCE = new TicketMessageService();

  private final Lazy<TicketMessageClient> ticketMessageClient =
      Lazy.of(() -> Launcher.getContext().getBean(TicketMessageClient.class));

  /**
   * Instantiates a new Ticket message service.
   */
  private TicketMessageService() {
  }

  /**
   * Create ticket message completable future.
   *
   * @param ticket        the ticket
   * @param ticketMessage the ticket message
   * @return the completable future
   */
  public CompletableFuture<TicketMessage> createTicketMessage(Ticket ticket,
      TicketMessage ticketMessage) {
    return CompletableFuture.supplyAsync(() -> ticketMessageClient.get()
            .createTicketMessage(ticket.getTicketId(), ticketMessage))
        .exceptionally(exception -> {
          LOGGER.error("Could not create ticket message {}", "", exception);
          return null;
        }).thenApplyAsync(message -> {
          LOGGER.info("Ticket message created: {}", message);
          return message;
        });
  }
}
