package dev.slne.discord.listener.message

import dev.slne.discord.ticket.Ticket
import lombok.AllArgsConstructor
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.message.GenericMessageEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import java.util.function.Consumer

@AllArgsConstructor
abstract class AbstractMessageListener<T : GenericMessageEvent?> :
    ListenerAdapter() {
    private val ticketService: TicketService? = null

    protected open fun handleEvent(event: T, ticket: Ticket) {
    }

    protected fun processEvent(event: T) {
        getTicketByChannel(event!!.getChannel()).ifPresent(Consumer { ticket: Ticket ->
            handleEvent(
                event,
                ticket
            )
        })
    }

    protected fun getTicketByChannel(channel: Channel): Optional<Ticket> {
        if (channel.getType() != ChannelType.TEXT) {
            return Optional.empty()
        }

        return ticketService.getTicketByChannelId(channel.getId())
    }
}
