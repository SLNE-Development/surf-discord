package dev.slne.discord.extensions

import dev.slne.discord.spring.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType

val Channel.ticket: Ticket?
    get() {
        if (type != ChannelType.GUILD_PRIVATE_THREAD) {
            return null
        }

        return TicketService.getTicketByThreadId(id)
    }