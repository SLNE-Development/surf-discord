package dev.slne.discord.spring.service.ticket;

import dev.slne.discord.spring.feign.client.TicketMemberClient;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.member.TicketMember;
import feign.FeignException;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * The type Ticket member service.
 */
@Service
public class TicketMemberService {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketMemberService");
  private final TicketMemberClient ticketMemberClient;

  @Autowired
  public TicketMemberService(TicketMemberClient ticketMemberClient) {
    this.ticketMemberClient = ticketMemberClient;
  }

  /**
   * Create ticket member completable future.
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return the completable future
   */
  @Async
  public CompletableFuture<TicketMember> createTicketMember(
      Ticket ticket,
      TicketMember ticketMember
  ) {
    try {
      final TicketMember createdMember = ticketMemberClient.createTicketMember(ticket.getTicketId(),
          ticketMember);
      return CompletableFuture.completedFuture(createdMember);
    } catch (FeignException e) {
      LOGGER.error("Failed to create ticket member for ticket {}.", ticket.getTicketId(), e);
      return CompletableFuture.completedFuture(null);
    }
  }

  /**
   * Update ticket member completable future.
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return the completable future
   */
  public CompletableFuture<TicketMember> updateTicketMember(Ticket ticket,
      TicketMember ticketMember) {
    return CompletableFuture.supplyAsync(() -> ticketMemberClient.get().updateTicketMember(
        ticket.getTicketId(),
        ticketMember.getMemberId(),
        ticketMember
    ));
  }

  /**
   * Delete ticket member completable future.
   *
   * @param ticket       the ticket
   * @param ticketMember the ticket member
   * @return the completable future
   */
  public CompletableFuture<Void> deleteTicketMember(Ticket ticket, TicketMember ticketMember) {
    return CompletableFuture.runAsync(() -> ticketMemberClient.get().deleteTicketMember(
        ticket.getTicketId(),
        ticketMember.getMemberId()
    ));
  }

}
