package dev.slne.discordold.extensions

import dev.slne.discordold.getBean
import dev.slne.discordold.persistence.service.ticket.TicketService
import dev.slne.discordold.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType

suspend fun Channel.ticketOrNull(): Ticket? {
    if (type != ChannelType.GUILD_PRIVATE_THREAD) {
        return null
    }

    return getBean<TicketService>().getTicketByThreadId(id)
}

suspend fun Channel.ticket(): Ticket {
    if (type != ChannelType.GUILD_PRIVATE_THREAD) {
        error("Channel $id is not a thread")
    }

    return getBean<TicketService>().getTicketByThreadId(id)
        ?: error("Ticket not found for thread $id")
}