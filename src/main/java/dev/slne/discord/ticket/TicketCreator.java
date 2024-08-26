package dev.slne.discord.ticket;

import dev.slne.discord.config.discord.GuildConfig;
import dev.slne.discord.config.ticket.TicketTypeConfig;
import dev.slne.discord.exception.ticket.DeleteTicketChannelException;
import dev.slne.discord.message.MessageManager;
import dev.slne.discord.message.Messages;
import dev.slne.discord.message.TimeFormatter;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.result.TicketCloseResult;
import dev.slne.discord.ticket.result.TicketCreateResult;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * The type Ticket creator.
 */
@Component
public class TicketCreator {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketCreator");

  private final TicketService ticketService;
  private final TicketChannelHelper ticketChannelHelper;
  private final MessageManager messageManager;

  @Autowired
  public TicketCreator(TicketService ticketService, TicketChannelHelper ticketChannelHelper,
      MessageManager messageManager) {
    this.ticketService = ticketService;
    this.ticketChannelHelper = ticketChannelHelper;
    this.messageManager = messageManager;
  }

  // region Open Ticket

  /**
   * Create ticket.
   *
   * @param ticket          the ticket
   * @param ticketName      the ticket name
   * @param channelCategory the channel category
   * @param runnable        the runnable
   */
  @Async
  public CompletableFuture<TicketCreateResult> createTicket(
      Ticket ticket,
      User author,
      String ticketName,
      Category channelCategory,
      Runnable runnable
  ) {

    final Ticket createdTicket = ticketService.createTicket(ticket).join();

    if (createdTicket == null) {
      return CompletableFuture.completedFuture(TicketCreateResult.ERROR);
    }

    ticketService.queueOrAddTicket(createdTicket);
    final TicketCreateResult result = createTicketChannel(ticket, author, ticketName, channelCategory,
        runnable).join();

    return CompletableFuture.completedFuture(result);
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
  protected CompletableFuture<TicketCreateResult> createTicketChannel(
      Ticket ticket,
      User author,
      String ticketName,
      Category channelCategory,
      Runnable runnable
  ) {
    final TicketCreateResult result = ticketChannelHelper.createTicketChannel(ticket, ticketName,
        channelCategory).join();

    if (result == null) {
      return CompletableFuture.completedFuture(TicketCreateResult.ERROR);
    }

    if (result != TicketCreateResult.SUCCESS) {
      return CompletableFuture.completedFuture(result);
    }

    runAfterOpen(ticket, author, runnable);
    return CompletableFuture.completedFuture(TicketCreateResult.SUCCESS);
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
      @NotNull Ticket ticket,
      User author,
      Runnable runnable
  ) {
    final TicketTypeConfig ticketTypeConfig = TicketTypeConfig.getConfig(ticket.getTicketType());
    final TextChannel channel = ticket.getChannel();

    if (channel == null) {
      return;
    }

//    ticket.printOpeningMessages(channel, ticketTypeConfig).join(); // TODO: 25.08.2024 15:40 - remove
    runnable.run();

    if (ticketTypeConfig.isShouldPrintWlQuery()) {
      messageManager.printUserWlQuery(author, channel);
    }
  }

  @Async
  public CompletableFuture<TicketCreateResult> openTicket(Ticket ticket, Runnable afterOpen) {
    final User author = ticket.getTicketAuthorNow();
    final TicketType ticketType = ticket.getTicketType();
    final String ticketChannelName = ticketChannelHelper.generateTicketName(ticketType, author);
    final Guild guild = ticket.getGuild();

    if (guild == null) {
      return CompletableFuture.completedFuture(TicketCreateResult.GUILD_NOT_FOUND);
    }

    final GuildConfig guildConfig = GuildConfig.getByGuild(guild);
    if (guildConfig == null) {
      return CompletableFuture.completedFuture(TicketCreateResult.GUILD_CONFIG_NOT_FOUND);
    }

    final String categoryId = guildConfig.getCategoryId();
    final Category channelCategory = guild.getCategoryById(categoryId);

    if (channelCategory == null) {
      return CompletableFuture.completedFuture(TicketCreateResult.CATEGORY_NOT_FOUND);
    }

    final boolean ticketExists = ticketChannelHelper.checkTicketExists(
        ticketChannelName,
        channelCategory,
        ticket.getTicketType(),
        author
    );

    if (ticketExists) {
      return CompletableFuture.completedFuture(TicketCreateResult.ALREADY_EXISTS);
    }

    final TicketCreateResult result = createTicket(ticket, author, ticketChannelName,
        channelCategory, afterOpen).join();

    return CompletableFuture.completedFuture(result);
  }
  // endregion

  // region Close Ticket
  @Async
  public CompletableFuture<TicketCloseResult> closeTicket(Ticket ticket, User closer,
      @Nullable String reason) {
    final TextChannel channel = ticket.getChannel();

    if (channel == null) {
      return CompletableFuture.completedFuture(TicketCloseResult.TICKET_NOT_FOUND);
    }

    ticket.setClosedById(closer.getId());
    ticket.setClosedByAvatarUrl(closer.getAvatarUrl());
    ticket.setClosedByName(closer.getName());
    ticket.setClosedAt(ZonedDateTime.now(TimeFormatter.EUROPE_BERLIN));
    ticket.setClosedReason(reason != null ? reason : Messages.DEFAULT_TICKET_CLOSED_REASON);

    final Ticket closedTicket = ticketService.closeTicket(ticket).join();

    if (closedTicket == null) {
      return CompletableFuture.completedFuture(TicketCloseResult.TICKET_REPOSITORY_ERROR);
    }

    try {
      ticketChannelHelper.deleteTicketChannel(ticket).join();
    } catch (DeleteTicketChannelException e) {
      LOGGER.error("Failed to delete ticket channel with id {}.", ticket.getTicketId(), e);
      return CompletableFuture.completedFuture(TicketCloseResult.TICKET_CHANNEL_NOT_CLOSABLE);
    }

    messageManager.sendTicketClosedMessages(ticket).join();
    LOGGER.debug("Ticket with id {} closed by {}.", ticket.getTicketId(), closer.getName());

    return CompletableFuture.completedFuture(TicketCloseResult.SUCCESS);
  }
  // endregion
}
