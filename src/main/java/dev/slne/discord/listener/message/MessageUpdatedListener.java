package dev.slne.discord.listener.message;

import dev.slne.discord.spring.annotation.DiscordListener;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * The type Message updated listener.
 */
@DiscordListener
public class MessageUpdatedListener extends AbstractMessageListener<MessageUpdateEvent> {

  @Autowired
  public MessageUpdatedListener(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  @Async
  protected void handleEvent(MessageUpdateEvent event, Ticket ticket) {
    if (event.getMessage().isWebhookMessage()) {
      return;
    }

    final TicketMessage ticketMessage = ticket.getTicketMessage(event.getMessageId());

    if (ticketMessage == null) {
      return;
    }

    final TicketMessage updatedTicketMessage = ticketMessage.update(event.getMessage()).join();

    if (updatedTicketMessage != null) {
      ticket.addRawTicketMessage(updatedTicketMessage);
    }
  }

  @Override
  public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
    processEvent(event);
  }
}
