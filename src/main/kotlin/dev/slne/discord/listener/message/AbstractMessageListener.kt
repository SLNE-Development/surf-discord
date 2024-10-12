package dev.slne.discord.listener.message

import dev.slne.discord.spring.service.ticket.TicketService
import dev.slne.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType

abstract class AbstractMessageListener {

    protected fun getTicketByChannel(channel: Channel): Ticket? {
        if (channel.type != ChannelType.GUILD_PRIVATE_THREAD) {
            return null
        }

        return TicketService.getTicketByChannelId(channel.id)
    }
}
