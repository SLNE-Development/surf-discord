package dev.slne.discord.spring.service.ticket;

import static com.google.common.base.Preconditions.checkState;

import dev.slne.discord.spring.feign.client.TicketClient;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannelHelper;
import dev.slne.discord.ticket.TicketCreator;
import dev.slne.discord.ticket.member.TicketMember;
import dev.slne.discord.ticket.message.TicketMessage;
import feign.FeignException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@ParametersAreNonnullByDefault
public class TicketService {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketService");

  private final TicketClient ticketClient;
  private final TicketMessageService ticketMessageService;
  private final TicketMemberService ticketMemberService;
  private final TicketChannelHelper ticketChannelHelper;
  private final TicketCreator ticketCreator;
  private volatile boolean fetched;
  private final ObjectList<Ticket> pendingTickets = ObjectLists.synchronize(
      new ObjectArrayList<>(1));
  private ObjectList<Ticket> tickets = ObjectLists.synchronize(new ObjectArrayList<>(1));

  public TicketService(TicketClient ticketClient, TicketMessageService ticketMessageService,
      TicketMemberService ticketMemberService, TicketChannelHelper ticketChannelHelper,
      TicketCreator ticketCreator) {
    this.ticketClient = ticketClient;
    this.ticketMessageService = ticketMessageService;
    this.ticketMemberService = ticketMemberService;
    this.ticketCreator = ticketCreator;
  }

  @Blocking
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

    try {
      final Ticket updated = ticketClient.updateTicket(ticket);
      ticket.updateFrom(updated);

      return CompletableFuture.completedFuture(updated);
    } catch (FeignException e) {
      LOGGER.error("Failed to close ticket with id {}.", ticket.getTicketId(), e);
      return CompletableFuture.completedFuture(null);
    }
  }

  @UnmodifiableView
  public ObjectList<Ticket> getTickets() {
    return ObjectLists.unmodifiable(tickets);
  }

  @Async
  public CompletableFuture<TicketMessage> addTicketMessage(Ticket ticket, TicketMessage message) {
    final TicketMessage createdMessage = ticketMessageService.createTicketMessage(ticket, message)
        .join();
    ticket.addRawTicketMessage(createdMessage);

    return CompletableFuture.completedFuture(createdMessage);
  }

  @Async
  public CompletableFuture<TicketMember> addTicketMember(Ticket ticket, TicketMember member) {
    final RestAction<User> memberRest = member.getMember();

    if (memberRest == null) {
      return CompletableFuture.completedFuture(null);
    }

    final User user = memberRest.complete();

    if (user == null || ticket.memberExists(user)) {
      return CompletableFuture.completedFuture(null);
    }

    final TicketMember createdMember = ticketMemberService.createTicketMember(ticket, member)
        .join();

    if (createdMember == null) {
      return CompletableFuture.completedFuture(null);
    } else {
      ticket.addRawTicketMember(createdMember);
      return CompletableFuture.completedFuture(createdMember);
    }
  }

  @Async
  public CompletableFuture<TicketMember> removeTicketMember(
      Ticket ticket,
      TicketMember member,
      @NotNull User remover
  ) {
    member.setRemovedByAvatarUrl(remover.getAvatarUrl());
    member.setRemovedById(remover.getId());
    member.setRemovedByName(remover.getName());

    final TicketMember removedMember = ticketMemberService.updateTicketMember(ticket, member)
        .join();

    if (removedMember != null) {
      ticket.removeRawTicketMember(removedMember);
    }

    return CompletableFuture.completedFuture(removedMember);
  }
}
