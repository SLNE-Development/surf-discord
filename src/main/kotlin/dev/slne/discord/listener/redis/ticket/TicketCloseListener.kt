package dev.slne.discord.listener.redis.ticket

import dev.slne.data.api.spring.redis.event.annotation.DataListener
import dev.slne.discord.Bootstrap
import dev.slne.discord.ticket.Ticket

/**
 * The type Ticket close listener.
 */
@DataListeners
class TicketCloseListener {
    /**
     * On ticket close.
     *
     * @param packet the packet
     */
    @DataListener(channels = TicketClosePacket.CHANNEL)
    fun onTicketClose(packet: TicketClosePacket) {
        val ticket: Ticket? = packet.getTicket()
        if (ticket == null) {
            return
        }

        MESSAGE_MANAGER.get().sendTicketClosedMessages(ticket).thenRunAsync({
            try {
                TICKET_CHANNEL_HELPER.get().deleteTicketChannel(ticket)
            } catch (e: DeleteTicketChannelException) {
                LOGGER.error("Error while deleting ticket channel through TicketCloseListener", e)
            }
        })
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("TicketCloseListener")
        private val MESSAGE_MANAGER: Lazy<MessageManager> = Lazy.of(
            { Bootstrap.getContext().getBean(MessageManager::class.java) })
        private val TICKET_CHANNEL_HELPER: Lazy<TicketChannelHelper> = Lazy.of(
            { Bootstrap.getContext().getBean(TicketChannelHelper::class.java) })
    }
}
