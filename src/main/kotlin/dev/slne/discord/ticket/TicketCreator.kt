package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.config.discord.getGuildConfig
import dev.slne.discord.config.ticket.TicketTypeConfig
import dev.slne.discord.config.ticket.getConfig
import dev.slne.discord.exception.ticket.DeleteTicketChannelException
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.Messages
import dev.slne.discord.message.TimeFormatter
import dev.slne.discord.spring.service.ticket.TicketService
import dev.slne.discord.ticket.TicketCreator.Companion.LOGGER
import dev.slne.discord.ticket.result.TicketCloseResult
import dev.slne.discord.ticket.result.TicketCreateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.CompletableFuture


object TicketCreator @Autowired constructor(
    private val ticketService: TicketService,
    private val ticketChannelHelper: TicketChannelHelper,
    private val messageManager: MessageManager
) {

    suspend fun createTicket(
        ticket: Ticket,
        author: User,
        ticketName: String,
        channelCategory: Category,
        runnable: Runnable
    ): TicketCreateResult = withContext(Dispatchers.IO) {
        val createdTicket = ticketService.createTicket(ticket).join()

        ticketService.queueOrAddTicket(createdTicket)

        createTicketChannel(ticket, author, ticketName, channelCategory, runnable)
    }

    protected suspend fun createTicketChannel(
        ticket: Ticket,
        author: User,
        ticketName: String,
        channelCategory: Category,
        runnable: Runnable
    ): TicketCreateResult =
        withContext(Dispatchers.IO) {
            ticketChannelHelper.createTicketChannel(
                ticket, ticketName,
                channelCategory
            ).join().run {
                if (this != TicketCreateResult.SUCCESS) {
                    return@run this
                }

                runAfterOpen(ticket, author, runnable)

                return@run TicketCreateResult.SUCCESS
            }
        }

    protected fun runAfterOpen(
        ticket: Ticket,
        author: User,
        runnable: Runnable
    ) {
        val ticketTypeConfig: TicketTypeConfig = ticket.ticketType.getConfig() ?: return
        val channel = ticket.thread ?: return

        runnable.run()

        if (ticketTypeConfig.shouldPrintWlQuery) {
            messageManager.printUserWlQuery(author, channel)
        }
    }

    suspend fun openTicket(ticket: Ticket, afterOpen: Runnable): TicketCreateResult {
        val author = ticket.ticketAuthor?.await() ?: return TicketCreateResult.AUTHOR_NOT_FOUND
        val ticketType = ticket.ticketType
        val ticketChannelName = ticketChannelHelper.generateTicketName(ticketType, author)
        val guild = ticket.guild ?: return TicketCreateResult.GUILD_NOT_FOUND

        val guildConfig = guild.getGuildConfig() ?: return TicketCreateResult.GUILD_CONFIG_NOT_FOUND

        val categoryId: String = guildConfig.categoryId

        val ticketExists = ticketChannelHelper.checkTicketExists(
            ticketChannelName,
            channelCategory,
            ticket.ticketType,
            author
        )

        if (ticketExists) {
            return CompletableFuture.completedFuture<TicketCreateResult>(TicketCreateResult.ALREADY_EXISTS)
        }

        val result: TicketCreateResult = createTicket(
            ticket, author, ticketChannelName,
            channelCategory, afterOpen
        ).join()

        return CompletableFuture.completedFuture<TicketCreateResult>(result)
    }

    // endregion
    // region Close Ticket
    suspend fun closeTicket(
        ticket: Ticket, closer: User,
        reason: String?
    ): TicketCloseResult {
        val channel = ticket.thread
            ?: return CompletableFuture.completedFuture<TicketCloseResult>(
                TicketCloseResult.TICKET_NOT_FOUND
            )

        ticket.closedById = closer.id
        ticket.closedByAvatarUrl = closer.avatarUrl
        ticket.closedByName = closer.name
        ticket.closedAt = ZonedDateTime.now(TimeFormatter.EUROPE_BERLIN)
        ticket.closedReason = reason
            ?: Messages.DEFAULT_TICKET_CLOSED_REASON

        val closedTicket: Ticket = ticketService.closeTicket(ticket).join()
            ?: return CompletableFuture.completedFuture<TicketCloseResult>(
                TicketCloseResult.TICKET_REPOSITORY_ERROR
            )

        try {
            ticketChannelHelper.deleteTicketChannel(ticket).join()
        } catch (e: DeleteTicketChannelException) {
            LOGGER.error("Failed to delete ticket channel with id {}.", ticket.ticketId, e)
            return CompletableFuture.completedFuture<TicketCloseResult>(TicketCloseResult.TICKET_CHANNEL_NOT_CLOSABLE)
        }

        messageManager.sendTicketClosedMessages(ticket).join()
        LOGGER.debug("Ticket with id {} closed by {}.", ticket.ticketId, closer.name)

        return CompletableFuture.completedFuture<TicketCloseResult>(TicketCloseResult.SUCCESS)
    } // endregion

    companion object {
        private val LOGGER = ComponentLogger.logger("TicketCreator")
    }
}
