package dev.slne.discordold.extensions

import dev.slne.discordold.exception.command.pre.PreTicketCommandException
import dev.slne.discordold.getBean
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel

suspend fun ThreadChannel.getTicket() = getBean<TicketService>().getTicketByThreadId(id)

suspend fun ThreadChannel.getTicketOrThrow() = getTicket() ?: throw PreTicketCommandException(
    translatable("error.ticket.no-ticket-channel")
)
