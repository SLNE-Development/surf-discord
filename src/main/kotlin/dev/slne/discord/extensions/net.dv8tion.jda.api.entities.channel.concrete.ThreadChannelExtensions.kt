package dev.slne.discord.extensions

import dev.slne.discord.exception.command.pre.PreTicketCommandException
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.service.ticket.TicketService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

fun ThreadChannel.getTicket() = TicketService.getTicketByThreadId(id)
fun ThreadChannel.getTicketOrThrow() = getTicket() ?: throw PreTicketCommandException(
    RawMessages.get("error.ticket.no-ticket-channel")
)
