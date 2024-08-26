package dev.slne.discord.listener.message;

import dev.slne.discord.annotation.DiscordListener;
import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.message.TicketMessage;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Message created listener.
 */
@DiscordListener
public class MessageCreatedListener extends AbstractMessageListener<MessageReceivedEvent> {

  @Autowired
  public MessageCreatedListener(TicketService ticketService) {
    super(ticketService);
  }

  @Override
  public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    processEvent(event);
  }

  @Override
  protected void handleEvent(MessageReceivedEvent event, Ticket ticket) {
    if (event.getMessage().isWebhookMessage()) {
      return;
    }

    ticket.addTicketMessage(TicketMessage.fromTicketAndMessage(ticket, event.getMessage()));
  }
}
