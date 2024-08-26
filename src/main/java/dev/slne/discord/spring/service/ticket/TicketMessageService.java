package dev.slne.discord.spring.service.ticket;

import dev.slne.discord.spring.feign.client.TicketMessageClient;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The type Ticket message service.
 */
@Service
public class TicketMessageService {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketMessageService");
  private final TicketMessageClient ticketMessageClient;

  public TicketMessageService(TicketMessageClient ticketMessageClient) {
    this.ticketMessageClient = ticketMessageClient;
  }

  /**
   * Create ticket message completable future.
   *
   * @param ticket        the ticket
   * @param ticketMessage the ticket message
   * @return the completable future
   */
  @Async
  public CompletableFuture<TicketMessage> createTicketMessage(@NotNull Ticket ticket,
      TicketMessage ticketMessage) {
    final TicketMessage createdMessage = ticketMessageClient.createTicketMessage(
        ticket.getTicketId(), ticketMessage);

    LOGGER.debug("Ticket message created: {}", createdMessage);
    return CompletableFuture.completedFuture(createdMessage);
  }
}
