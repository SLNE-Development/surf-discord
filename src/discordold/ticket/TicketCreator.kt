package dev.slne.discordold.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discordold.discord.interaction.command.getGuildConfig
import dev.slne.discordold.exception.ticket.DeleteTicketChannelException
import dev.slne.discordold.message.MessageManager
import dev.slne.discordold.message.Messages
import dev.slne.discordold.persistence.service.ticket.TicketService
import dev.slne.discordold.ticket.result.TicketCloseResult
import dev.slne.discordold.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.springframework.stereotype.Service
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
@Service
class TicketCreator(
    private val ticketService: TicketService,
    private val channelHelper: TicketChannelHelper,
    private val messageManager: MessageManager
) {

    private val logger = ComponentLogger.logger()

    private final suspend fun createTicket(
        ticket: Ticket,
        author: User,
        ticketName: String,
        ticketChannel: TextChannel,
        callback: suspend () -> Unit
    ): TicketCreateResult {
        contract {
            callsInPlace(callback, InvocationKind.AT_MOST_ONCE)
        }

        ticketService.saveTicket(ticket)

        return createTicketChannel(ticket, author, ticketName, ticketChannel, callback)
    }

    private final suspend fun createTicketChannel(
        ticket: Ticket,
        author: User,
        ticketName: String,
        ticketChannel: TextChannel,
        callback: suspend () -> Unit
    ): TicketCreateResult {
        contract {
            callsInPlace(callback, InvocationKind.AT_MOST_ONCE)
        }

        val result = channelHelper.createThread(ticket, ticketName, ticketChannel)

        if (result != TicketCreateResult.SUCCESS) {
            return result
        }

        runAfterOpen(ticket, author, callback)

        return TicketCreateResult.SUCCESS
    }

    private final suspend fun runAfterOpen(
        ticket: Ticket,
        author: User,
        runnable: suspend () -> Unit
    ) {
        contract {
            callsInPlace(runnable, InvocationKind.EXACTLY_ONCE)
        }

        val channel = ticket.thread
        checkNotNull(channel) { "Ticket thread is not yet set." }

        runnable()

        if (ticket.ticketType.shouldPrintWlQuery) {
            runCatching { messageManager.printUserWlQuery(author, channel, null) }
        }
    }

    final suspend fun openTicket(
        ticket: Ticket,
        afterOpen: suspend () -> Unit = {}
    ): TicketCreateResult {
        contract {
            callsInPlace(afterOpen, InvocationKind.AT_MOST_ONCE)
        }

        val author = ticket.author.await() ?: return TicketCreateResult.AUTHOR_NOT_FOUND
        val ticketType = ticket.ticketType
        val ticketChannelName = channelHelper.generateTicketName(ticketType, author)
        val guild = ticket.guild ?: return TicketCreateResult.GUILD_NOT_FOUND

        val guildConfig =
            guild.getGuildConfig()?.discordGuild ?: return TicketCreateResult.GUILD_CONFIG_NOT_FOUND
        val categoryId =
            guildConfig.ticketChannels[ticketType] ?: return TicketCreateResult.CHANNEL_NOT_FOUND

        val channel =
            guild.getTextChannelById(categoryId) ?: return TicketCreateResult.CHANNEL_NOT_FOUND
        val ticketExists = channelHelper.checkTicketExists(
            ticketChannelName,
            channel,
            ticketType,
            author
        )

        if (ticketExists) return TicketCreateResult.ALREADY_EXISTS

        return createTicket(ticket, author, ticketChannelName, channel, afterOpen)
    }

    suspend fun closeTicket(
        ticket: Ticket,
        closer: User,
        reason: String?
    ): TicketCloseResult {
        if (ticket.isClosed) return TicketCloseResult.TICKET_ALREADY_CLOSED
        if (ticket.thread == null) return TicketCloseResult.TICKET_NOT_FOUND

        ticket.close(closer, reason ?: Messages.DEFAULT_TICKET_CLOSED_REASON)

        try {
            ticketService.saveTicket(ticket)
            runCatching {
                messageManager.sendTicketClosedMessages(ticket)
                messageManager.sendTicketClosedUserPrivateMessage(ticket)
            }
            channelHelper.closeThread(ticket)
        } catch (e: DeleteTicketChannelException) {
            logger.error("Failed to close ticket thread with id {}.", ticket.ticketId, e)
            return TicketCloseResult.TICKET_CHANNEL_NOT_CLOSABLE
        }

        logger.debug("Ticket with id {} closed by {}.", ticket.ticketId, closer.name)


        return TicketCloseResult.SUCCESS
    }
}
