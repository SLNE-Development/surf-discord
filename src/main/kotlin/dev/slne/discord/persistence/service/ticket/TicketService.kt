package dev.slne.discord.persistence.service.ticket

import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectLists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*
import kotlin.system.measureTimeMillis

object TicketService {

    private val logger = ComponentLogger.logger(TicketService::class.java)

    private var fetched = true // Only for testing
    private val pendingTickets: ObjectList<Ticket> = ObjectLists.synchronize(
        ObjectArrayList(1)
    )
    var tickets: ObjectList<Ticket> = ObjectLists.synchronize(ObjectArrayList(1))

    suspend fun fetchActiveTickets() = withContext(Dispatchers.IO) {
        fetched = false

        val ms = measureTimeMillis {
            TicketRepository.findAll().forEach { tickets.add(it) }
        }

        logger.info("Fetched {} tickets in {}ms.", tickets.size, ms)
        fetched = true
        popQueue()
    }

    suspend fun saveTicket(ticket: Ticket) = TicketRepository.save(ticket)


    private fun popQueue() {
        if (fetched) {
            tickets.addAll(pendingTickets)
            pendingTickets.clear()
        }
    }

    fun queueOrAddTicket(ticket: Ticket) {
        if (fetched) {
            tickets.add(ticket)
        } else {
            pendingTickets.add(ticket)
        }
    }

    fun removeTicket(ticket: Ticket) {
        tickets.remove(ticket)
        pendingTickets.remove(ticket)
    }

    fun getTicketById(id: UUID) = tickets.firstOrNull { it.ticketId == id }

    fun getTicketByThreadId(threadId: String) = tickets.firstOrNull { it.threadId == threadId }

    fun createTicket(ticket: Ticket): Ticket = ticket

    fun updateTicket(ticket: Ticket): Ticket = ticket

    fun closeTicket(ticket: Ticket): Ticket = ticket

    suspend fun addTicketMessage(
        ticket: Ticket,
        message: TicketMessage
    ) = ticket.addRawTicketMessage(TicketMessageService.createTicketMessage(ticket, message))

}
