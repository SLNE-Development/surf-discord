package dev.slne.discord.ticket;

import dev.slne.discord.config.ticket.TicketTypeConfig;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.result.TicketCreateResult;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * The type Ticket creator.
 */
@Component
public class TicketCreator {

  private final TicketService ticketService;
  private final TicketChannelHelper ticketChannelHelper;

  @Autowired
  public TicketCreator(TicketService ticketService, TicketChannelHelper ticketChannelHelper) {
    this.ticketService = ticketService;
    this.ticketChannelHelper = ticketChannelHelper;
  }

  /**
   * Create ticket.
   *
   * @param future          the future
   * @param ticket          the ticket
   * @param ticketName      the ticket name
   * @param channelCategory the channel category
   * @param runnable        the runnable
   */
  @Async
  public void createTicket(
      CompletableFuture<TicketCreateResult> future,
      Ticket ticket,
      String ticketName,
      Category channelCategory,
      Runnable runnable
  ) {

    final Ticket createdTicket = ticketService.createTicket(ticket).join();

    if (createdTicket == null) {
      future.complete(TicketCreateResult.ERROR);
      return;
    }

    ticketService.queueOrAddTicket(createdTicket);
    createTicketChannel(future, ticket, ticketName, channelCategory, runnable);
  }

  /**
   * Create ticket channel.
   *
   * @param future          the future
   * @param ticket          the ticket
   * @param ticketName      the ticket name
   * @param channelCategory the channel category
   * @param runnable        the runnable
   */
  @Async
  protected void createTicketChannel(
      CompletableFuture<TicketCreateResult> future,
      Ticket ticket,
      String ticketName,
      Category channelCategory,
      Runnable runnable
  ) {
    final TicketCreateResult result = ticketChannelHelper.createTicketChannel(ticket, ticketName,
        channelCategory).join();

    if (result == null) {
      future.complete(TicketCreateResult.ERROR);
      return;
    }

    if (result != TicketCreateResult.SUCCESS) {
      future.complete(result);
      return;
    }

    runAfterOpen(future, ticket, runnable);
  }

  /**
   * Run after open.
   *
   * @param future   the future
   * @param ticket   the ticket
   * @param runnable the runnable
   */
  @Async
  protected void runAfterOpen(
      CompletableFuture<TicketCreateResult> future,
      @NotNull Ticket ticket,
      Runnable runnable
  ) {
    final TicketTypeConfig ticketTypeConfig = TicketTypeConfig.getConfig(ticket.getTicketType());

    ticket.printOpeningMessages(ticketTypeConfig).join();

    if (ticketTypeConfig.isShouldPrintWlQuery()) {
      ticket.printWlQueryEmbeds();
    }

    ticket.afterOpen().join();
    runnable.run();
    future.complete(TicketCreateResult.SUCCESS);
  }
}
