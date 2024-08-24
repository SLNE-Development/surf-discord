package dev.slne.discord.listener.message;

import dev.slne.discord.spring.annotation.DiscordListener;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.message.TicketMessage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Message deleted listener.
 */
@DiscordListener
public class MessageDeletedListener extends AbstractMessageListener<GenericMessageEvent> {

  @Autowired
  public MessageDeletedListener(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
    deleteMessage(event.getChannel(), List.of(event.getMessageId()));
  }

  @Override
  public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
    deleteMessage(event.getChannel(), event.getMessageIds());
  }

  /**
   * Deletes the message from the ticket.
   *
   * @param channel    The channel the message was deleted from.
   * @param messageIds The message ids of the deleted messages.
   */
  @Async
  protected void deleteMessage(MessageChannel channel, List<String> messageIds) {
    getTicketByChannel(channel).ifPresent(ticket -> messageIds.stream()
        .map(ticket::getTicketMessage) // Get the ticket message by id
        .filter(Objects::nonNull)
        .map(TicketMessage::delete) // Delete the message
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .forEach(ticket::addRawTicketMessage)); // Add the raw message to the ticket
  }
}
