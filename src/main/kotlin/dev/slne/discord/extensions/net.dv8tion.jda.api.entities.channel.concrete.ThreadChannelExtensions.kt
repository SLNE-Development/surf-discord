package dev.slne.discord.extensions

import dev.slne.discord.exception.command.pre.PreTicketCommandException
import dev.slne.discord.getBean
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

fun ThreadChannel.getTicket() = getBean<TicketService>().getTicketByThreadId(id)

fun ThreadChannel.getTicketOrThrow() = getTicket() ?: throw PreTicketCommandException(
    translatable("error.ticket.no-ticket-channel")
)
