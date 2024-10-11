package dev.slne.discord.spring.service.ticket

import dev.slne.discord.spring.feign.client.TicketMessageClient
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

/**
 * The type Ticket message service.
 */
@Service
class TicketMessageService(private val ticketMessageClient: TicketMessageClient) {
    /**
     * Create ticket message completable future.
     *
     * @param ticket        the ticket
     * @param ticketMessage the ticket message
     * @return the completable future
     */
    @Async
    fun createTicketMessage(
        ticket: Ticket,
        ticketMessage: TicketMessage?
    ): CompletableFuture<TicketMessage?> {
        val createdMessage = ticketMessageClient.createTicketMessage(
            ticket.ticketId, ticketMessage
        )

        LOGGER.debug("Ticket message created: {}", createdMessage)
        return CompletableFuture.completedFuture(createdMessage)
    }

    companion object {
        private val LOGGER = ComponentLogger.logger("TicketMessageService")
    }
}
