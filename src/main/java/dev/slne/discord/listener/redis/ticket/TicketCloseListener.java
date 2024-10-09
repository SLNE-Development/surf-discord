package dev.slne.discord.listener.redis.ticket;

import dev.slne.data.api.spring.redis.event.annotation.DataListener;
import dev.slne.data.api.spring.redis.event.annotation.DataListeners;
import dev.slne.discord.Bootstrap;
import dev.slne.discord.datasource.redis.packets.TicketClosePacket;
import dev.slne.discord.exception.ticket.DeleteTicketChannelException;
import dev.slne.discord.message.MessageManager;
import dev.slne.discord.ticket.Ticket;
import dev.slne.discord.ticket.TicketChannelHelper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.springframework.data.util.Lazy;

/**
 * The type Ticket close listener.
 */
@DataListeners
public class TicketCloseListener {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("TicketCloseListener");
  private static final Lazy<MessageManager> MESSAGE_MANAGER = Lazy.of(
      () -> Bootstrap.getContext().getBean(MessageManager.class));
  private static final Lazy<TicketChannelHelper> TICKET_CHANNEL_HELPER = Lazy.of(
      () -> Bootstrap.getContext().getBean(TicketChannelHelper.class));

  /**
   * On ticket close.
   *
   * @param packet the packet
   */
  @DataListener(channels = TicketClosePacket.CHANNEL)
  public void onTicketClose(TicketClosePacket packet) {
    final Ticket ticket = packet.getTicket();
    if (ticket == null) {
      return;
    }

    MESSAGE_MANAGER.get().sendTicketClosedMessages(ticket).thenRunAsync(() -> {
      try {
        TICKET_CHANNEL_HELPER.get().deleteTicketChannel(ticket);
      } catch (DeleteTicketChannelException e) {
        LOGGER.error("Error while deleting ticket channel through TicketCloseListener", e);
      }
    });
  }
}
