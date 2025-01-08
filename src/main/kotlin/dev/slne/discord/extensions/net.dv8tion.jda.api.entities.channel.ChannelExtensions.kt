package dev.slne.discord.extensions

import dev.slne.discord.getBean
import dev.slne.discord.persistence.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType

val Channel.ticket: Ticket
    get() {
        if (type != ChannelType.GUILD_PRIVATE_THREAD) {
            error("Channel $id is not a thread")
        }

        return getBean<TicketService>().getTicketByThreadId(id)
            ?: error("Ticket not found for thread $id")
    }