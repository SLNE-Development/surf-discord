package dev.slne.discord.spring.service.ticket

import com.google.common.base.Preconditions.checkState
import dev.slne.discord.spring.feign.client.TicketClient
import dev.slne.discord.spring.service.ticket.TicketService.Companion.LOGGER
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketChannelHelper
import dev.slne.discord.ticket.TicketCreator
import dev.slne.discord.ticket.member.TicketMember
import dev.slne.discord.ticket.message.TicketMessage
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.UnmodifiableView
import org.springframework.scheduling.annotation.Async
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.Volatile


object TicketService(
    private val ticketClient: TicketClient, private val ticketMessageService: TicketMessageService,
    private val ticketMemberService: TicketMemberService, ticketChannelHelper: TicketChannelHelper?,
    ticketCreator: TicketCreator
) {
    private val ticketChannelHelper: TicketChannelHelper? = null
    private val ticketCreator: TicketCreator = ticketCreator

    @Volatile
    private var fetched = false
    private val pendingTickets: ObjectList<Ticket?> = ObjectLists.synchronize(
        ObjectArrayList(1)
    )
    private var tickets: ObjectList<Ticket?> = ObjectLists.synchronize(ObjectArrayList(1))

    @Blocking
    fun fetchActiveTickets() {
        fetched = false
        val start = System.currentTimeMillis()

        val fetchedTickets = ticketClient.activeTickets

        if (fetchedTickets != null) {
            tickets = ObjectLists.synchronize(ObjectArrayList(fetchedTickets))
        }

        val end = System.currentTimeMillis()
        val time = end - start

        LOGGER.info("Fetched {} tickets in {}ms.", tickets.size, time)
        fetched = true
        popQueue()
    }

    private fun popQueue() {
        if (fetched) {
            tickets.addAll(pendingTickets)
            pendingTickets.clear()
        }
    }

    fun queueOrAddTicket(ticket: Ticket?) {
        if (fetched) {
            tickets.add(ticket)
        } else {
            pendingTickets.add(ticket)
        }
    }

    fun removeTicket(ticket: Ticket?) {
        tickets.remove(ticket)
        pendingTickets.remove(ticket)
    }

    fun getTicketById(id: UUID): Optional<Ticket?> {
        return tickets.stream()
            .filter { ticket: Ticket? -> ticket!!.ticketId == id }
            .findFirst()
    }

    fun getTicketByChannelId(channelId: String): Ticket? {
        return tickets.stream()
            .filter { ticket: Ticket? -> ticket!!.threadId == channelId }
            .findFirst()
    }

    @Async
    fun createTicket(ticket: Ticket): CompletableFuture<Ticket> {
        val createdTicket = ticketClient.createTicket(ticket)
        ticket.updateFrom(createdTicket!!)

        return CompletableFuture.completedFuture(ticket)
    }

    @Async
    fun updateTicket(ticket: Ticket): CompletableFuture<Ticket?> {
        checkState(ticket.hasTicketId(), "Ticket must have a ticket id to be updated")

        return CompletableFuture.completedFuture(
            ticketClient.updateTicket(ticket)
        )
    }

    @Async
    fun closeTicket(ticket: Ticket): CompletableFuture<Ticket?> {
        checkState(ticket.hasTicketId(), "Ticket must have a ticket id to be closed")

        try {
            val updated = ticketClient.updateTicket(ticket)
            ticket.updateFrom(updated!!)

            return CompletableFuture.completedFuture(updated)
        } catch (e: FeignException) {
            LOGGER.error("Failed to close ticket with id {}.", ticket.ticketId, e)
            return CompletableFuture.completedFuture(null)
        }
    }

    fun getTickets(): @UnmodifiableView ObjectList<Ticket?> {
        return ObjectLists.unmodifiable(tickets)
    }

    @Async
    fun addTicketMessage(
        ticket: Ticket,
        message: TicketMessage?
    ): CompletableFuture<TicketMessage?> {
        val createdMessage: TicketMessage? =
            ticketMessageService.createTicketMessage(ticket, message)
                .join()
        ticket.addRawTicketMessage(createdMessage)

        return CompletableFuture.completedFuture<TicketMessage?>(createdMessage)
    }

    @Async
    fun addTicketMember(ticket: Ticket, member: TicketMember): CompletableFuture<TicketMember?> {
        val memberRest: RestAction<User> = member.member
            ?: return CompletableFuture.completedFuture(null)

        val user: User = memberRest.complete()

        if (user == null || ticket.memberExists(user)) {
            return CompletableFuture.completedFuture(null)
        }

        val createdMember: TicketMember? = ticketMemberService.createTicketMember(ticket, member)
            .join()

        if (createdMember == null) {
            return CompletableFuture.completedFuture(null)
        } else {
            ticket.addRawTicketMember(createdMember)
            return CompletableFuture.completedFuture<TicketMember?>(createdMember)
        }
    }

    @Async
    fun removeTicketMember(
        ticket: Ticket,
        member: TicketMember,
        remover: User
    ): CompletableFuture<TicketMember?> {
        member.removedByAvatarUrl = remover.avatarUrl
        member.removedById = remover.id
        member.removedByName = remover.name

        val removedMember: TicketMember? = ticketMemberService.updateTicketMember(ticket, member)
            .join()

        if (removedMember != null) {
            ticket.removeRawTicketMember(removedMember)
        }

        return CompletableFuture.completedFuture<TicketMember?>(removedMember)
    }

    companion object {
        private val LOGGER: ComponentLogger = ComponentLogger.logger("TicketService")
    }
}
