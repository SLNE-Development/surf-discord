package dev.slne.discord.spring.service.ticket;

import static com.google.common.base.Preconditions.checkState;

import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.spring.feign.client.TicketClient;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.UnmodifiableView;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@ParametersAreNonnullByDefault
public class TicketService {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketService");

  private final TicketClient ticketClient;
  private volatile boolean fetched;
  private final ObjectList<Ticket> pendingTickets = ObjectLists.synchronize(
      new ObjectArrayList<>(1));
  private ObjectList<Ticket> tickets = ObjectLists.synchronize(new ObjectArrayList<>(1));

  public TicketService(TicketClient ticketClient) {
    this.ticketClient = ticketClient;
  }

  @Async
  public void fetchActiveTickets() {
    fetched = false;
    final long start = System.currentTimeMillis();

    final List<Ticket> fetchedTickets = ticketClient.getActiveTickets();

    if (fetchedTickets != null) {
      tickets = ObjectLists.synchronize(new ObjectArrayList<>(fetchedTickets));
    }

    final long end = System.currentTimeMillis();
    final long time = end - start;

    LOGGER.info("Fetched {} tickets in {}ms.", tickets.size(), time);
    fetched = true;
    popQueue();
  }

  private void popQueue() {
    if (fetched) {
      tickets.addAll(pendingTickets);
      pendingTickets.clear();
    }
  }

  public void queueOrAddTicket(Ticket ticket) {
    if (fetched) {
      tickets.add(ticket);
    } else {
      pendingTickets.add(ticket);
    }
  }

  public void removeTicket(Ticket ticket) {
    tickets.remove(ticket);
    pendingTickets.remove(ticket);
  }

  public Optional<Ticket> getTicketById(UUID id) {
    return tickets.stream()
        .filter(ticket -> ticket.getTicketId().equals(id))
        .findFirst();
  }

  public Optional<Ticket> getTicketByChannelId(String channelId) {
    return tickets.stream()
        .filter(ticket -> ticket.getChannelId().equals(channelId))
        .findFirst();
  }

  @Async
  public CompletableFuture<Ticket> createTicket(Ticket ticket) {
    final Ticket createdTicket = ticketClient.createTicket(ticket);
    ticket.updateFrom(createdTicket);

    return CompletableFuture.completedFuture(ticket);
  }

  @Async
  public CompletableFuture<Ticket> updateTicket(Ticket ticket) {
    checkState(ticket.hasTicketId(), "Ticket must have a ticket id to be updated");

    return CompletableFuture.completedFuture(ticketClient.updateTicket(ticket));
  }

  @Async
  public CompletableFuture<Ticket> closeTicket(Ticket ticket) {
    checkState(ticket.hasTicketId(), "Ticket must have a ticket id to be closed");

    final Ticket updated = ticketClient.updateTicket(ticket);
    ticket.updateFrom(updated);

    return CompletableFuture.completedFuture(updated);
  }

  @UnmodifiableView
  public ObjectList<Ticket> getTickets() {
    return ObjectLists.unmodifiable(tickets);
  }
}
