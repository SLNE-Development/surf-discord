package dev.slne.discord.spring.service.ticket

import dev.slne.discord.spring.feign.client.TicketMemberClient
import dev.slne.discord.ticket.Ticket
import feign.FeignException
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * The type Ticket member service.
 */
@Service
class TicketMemberService @Autowired constructor(private val ticketMemberClient: TicketMemberClient) {
    /**
     * Create ticket member completable future.
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return the completable future
     */
    @Async
    fun createTicketMember(
        ticket: Ticket,
        ticketMember: TicketMember?
    ): CompletableFuture<TicketMember?> {
        try {
            val createdMember: TicketMember? = ticketMemberClient.createTicketMember(
                ticket.ticketId,
                ticketMember
            )
            return CompletableFuture.completedFuture<TicketMember?>(createdMember)
        } catch (e: FeignException) {
            LOGGER.error("Failed to create ticket member for ticket {}.", ticket.ticketId, e)
            return CompletableFuture.completedFuture(null)
        }
    }

    /**
     * Update ticket member completable future.
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return the completable future
     */
    fun updateTicketMember(
        ticket: Ticket,
        ticketMember: TicketMember
    ): CompletableFuture<TicketMember?> {
        return CompletableFuture.supplyAsync<U?>(Supplier<U?> {
            ticketMemberClient.get().updateTicketMember(
                ticket.ticketId,
                ticketMember.memberId,
                ticketMember
            )
        })
    }

    /**
     * Delete ticket member completable future.
     *
     * @param ticket       the ticket
     * @param ticketMember the ticket member
     * @return the completable future
     */
    fun deleteTicketMember(ticket: Ticket, ticketMember: TicketMember): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            ticketMemberClient.get().deleteTicketMember(
                ticket.ticketId,
                ticketMember.memberId
            )
        }
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("TicketMemberService")
    }
}
