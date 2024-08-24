package dev.slne.discord.listener.message;

import dev.slne.discord.spring.service.ticket.TicketService;
import dev.slne.discord.ticket.Ticket;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public abstract class AbstractMessageListener<T extends GenericMessageEvent> extends
    ListenerAdapter {

  private final TicketService ticketService;

  protected void handleEvent(T event, Ticket ticket) {
  }

  protected final void processEvent(@NotNull T event) {
    getTicketByChannel(event.getChannel()).ifPresent(ticket -> handleEvent(event, ticket));
  }

  protected Optional<Ticket> getTicketByChannel(Channel channel) {
    if (!channel.getType().equals(ChannelType.TEXT)) {
      return Optional.empty();
    }

    return ticketService.getTicketByChannelId(channel.getId());
  }
}
