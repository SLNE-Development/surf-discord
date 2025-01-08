package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import dev.slne.discord.util.freeze
import dev.slne.discord.util.mutableObjectSetOf
import dev.slne.discord.util.synchronize
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class TicketService(private val ticketRepository: TicketRepository) {

    private val logger = ComponentLogger.logger()

    private var fetched = false
    private val pendingTickets = mutableObjectSetOf<Ticket>().synchronize()
    private val _tickets = mutableObjectSetOf<Ticket>().synchronize()
    val tickets = _tickets.freeze()

    @PostConstruct
    fun fetchActiveTickets() = runBlocking {
        withContext(Dispatchers.IO) {
            fetched = false
            _tickets.clear()

            val ms = measureTimeMillis {
                _tickets.addAll(ticketRepository.findByClosedAtNull())
            }

            logger.info("Fetched {} tickets in {}ms.", tickets.size, ms)
            fetched = true
            popQueue()
        }
    }

    suspend fun saveTicket(ticket: Ticket): Ticket = withContext(Dispatchers.IO) {
        ticketRepository.save(ticket)
        _tickets.add(ticket)

        return@withContext ticket
    }

    private fun popQueue() {
        if (fetched) {
            _tickets.addAll(pendingTickets)
            pendingTickets.clear()
        }
    }

    fun queueOrAddTicket(ticket: Ticket) {
        if (fetched) {
            _tickets.add(ticket)
        } else {
            pendingTickets.add(ticket)
        }
    }

    fun removeTicket(ticket: Ticket) {
        _tickets.remove(ticket)
        pendingTickets.remove(ticket)
    }

    fun addReopenedTicket(ticket: Ticket) {
        _tickets.add(ticket)
    }

    fun getTicketByThreadId(threadId: String) = _tickets.firstOrNull { it.threadId == threadId }

}
