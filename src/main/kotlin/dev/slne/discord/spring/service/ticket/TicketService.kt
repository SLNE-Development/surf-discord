package dev.slne.discord.spring.service.ticket

import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.message.TicketMessage
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.*

object TicketService {

    private val logger = ComponentLogger.logger(TicketService::class.java)

    private var fetched = false
    private val pendingTickets: ObjectList<Ticket> = ObjectLists.synchronize(
        ObjectArrayList(1)
    )
    var tickets: ObjectList<Ticket> = ObjectLists.synchronize(ObjectArrayList(1))

    suspend fun fetchActiveTickets() {
        fetched = false
        val start = System.currentTimeMillis()

        TODO("Implement")

        val end = System.currentTimeMillis()
        val time = end - start

        logger.info("Fetched {} tickets in {}ms.", tickets.size, time)
        fetched = true
        popQueue()
    }

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

    fun createTicket(ticket: Ticket): Ticket = TODO("Implement")

    fun updateTicket(ticket: Ticket): Ticket = TODO("Implement")

    fun closeTicket(ticket: Ticket): Ticket = TODO("Implement")

    suspend fun addTicketMessage(
        ticket: Ticket,
        message: TicketMessage
    ) = ticket.addRawTicketMessage(TicketMessageService.createTicketMessage(ticket, message))

}
