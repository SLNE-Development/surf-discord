package dev.slne.surf.discord.ticket.database.ticket

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.ticket.Ticket
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange("/ticket")
interface TicketCrudRepository {
    @GetExchange
    suspend fun findAll(): List<Ticket>

    @GetExchange("/{ticketId}")
    suspend fun findByTicketId(@PathVariable ticketId: String): Ticket?

    companion object {
        val CLIENT by lazy {
            getBean<WebClientConfiguration>()
                .webClientFactory()
                .createClient<TicketCrudRepository>()
        }
    }
}