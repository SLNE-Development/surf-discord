package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.discord.interaction.command.getGuildConfig
import dev.slne.discord.exception.ticket.DeleteTicketChannelException
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.Messages
import dev.slne.discord.message.toEuropeBerlin
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.result.TicketCloseResult
import dev.slne.discord.ticket.result.TicketCreateResult
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.time.ZonedDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
object TicketCreator {

    private val logger = ComponentLogger.logger()

    private suspend fun createTicket(
        ticket: Ticket,
        author: User,
        ticketName: String,
        ticketChannel: TextChannel,
        callback: suspend () -> Unit
    ): TicketCreateResult {
        contract {
            callsInPlace(callback, InvocationKind.EXACTLY_ONCE)
        }

        val createdTicket = TicketService.createTicket(ticket)
        TicketService.queueOrAddTicket(createdTicket)

        return createTicketChannel(ticket, author, ticketName, ticketChannel, callback)
    }

    private suspend fun createTicketChannel(
        ticket: Ticket,
        author: User,
        ticketName: String,
        ticketChannel: TextChannel,
        callback: suspend () -> Unit
    ): TicketCreateResult {
        contract {
            callsInPlace(callback, InvocationKind.EXACTLY_ONCE)
        }

        val result = TicketChannelHelper.createThread(ticket, ticketName, ticketChannel)

        if (result != TicketCreateResult.SUCCESS) {
            return result
        }

        runAfterOpen(ticket, author, callback)

        return TicketCreateResult.SUCCESS
    }

    private suspend fun runAfterOpen(
        ticket: Ticket,
        author: User,
        runnable: suspend () -> Unit
    ) {
        contract {
            callsInPlace(runnable, InvocationKind.EXACTLY_ONCE)
        }

        val ticketType = ticket.ticketType
        val channel = ticket.thread ?: return

        runnable()

        if (ticketType.shouldPrintWlQuery) {
            MessageManager.printUserWlQuery(author, channel)
        }
    }

    suspend fun openTicket(ticket: Ticket, afterOpen: suspend () -> Unit = {}): TicketCreateResult {
        contract {
            callsInPlace(afterOpen, InvocationKind.EXACTLY_ONCE)
        }

        val author = ticket.ticketAuthor?.await() ?: return TicketCreateResult.AUTHOR_NOT_FOUND
        val ticketType = ticket.ticketType
        val ticketChannelName = TicketChannelHelper.generateTicketName(ticketType, author)
        val guild = ticket.guild ?: return TicketCreateResult.GUILD_NOT_FOUND

        val guildConfig =
            guild.getGuildConfig()?.discordGuild ?: return TicketCreateResult.GUILD_CONFIG_NOT_FOUND
        val categoryId =
            guildConfig.ticketChannels[ticketType] ?: return TicketCreateResult.CHANNEL_NOT_FOUND

        val channel =
            guild.getTextChannelById(categoryId) ?: return TicketCreateResult.CHANNEL_NOT_FOUND
        val ticketExists = TicketChannelHelper.checkTicketExists(
            ticketChannelName,
            channel,
            ticket.ticketType,
            author
        )

        if (ticketExists) {
            return TicketCreateResult.ALREADY_EXISTS
        }

        return createTicket(ticket, author, ticketChannelName, channel, afterOpen)
    }


    suspend fun closeTicket(
        ticket: Ticket,
        closer: User,
        reason: String?
    ): TicketCloseResult {
        with(ticket) {
            thread ?: return TicketCloseResult.TICKET_NOT_FOUND

            closedById = closer.id
            closedByAvatarUrl = closer.avatarUrl
            closedByName = closer.name
            closedAt = ZonedDateTime.now().toEuropeBerlin()
            closedReason = reason ?: Messages.DEFAULT_TICKET_CLOSED_REASON
        }

        try {
            MessageManager.sendTicketClosedMessages(ticket)
            TicketService.closeTicket(ticket)
            TicketChannelHelper.closeThread(ticket)
        } catch (e: DeleteTicketChannelException) {
            logger.error("Failed to close ticket thread with id {}.", ticket.ticketId, e)
            return TicketCloseResult.TICKET_CHANNEL_NOT_CLOSABLE
        }

        logger.debug("Ticket with id {} closed by {}.", ticket.ticketId, closer.name)
        return TicketCloseResult.SUCCESS
    }
}
