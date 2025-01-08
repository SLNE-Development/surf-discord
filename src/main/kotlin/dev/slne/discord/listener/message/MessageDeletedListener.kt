package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticket
import dev.slne.discord.persistence.service.ticket.TicketService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent

class MessageDeletedListener(jda: JDA, private val ticketService: TicketService) {

    init {
        jda.listener<MessageDeleteEvent> {
            deleteMessage(it.channel, listOf(it.messageId))
        }

        jda.listener<MessageBulkDeleteEvent> { event ->
            deleteMessage(event.channel, event.messageIds)
        }
    }

    private suspend fun deleteMessage(channel: MessageChannel, messageIds: List<String>) {
        val ticket = channel.ticket

        messageIds
            .mapNotNull { ticket.getTicketMessage(it) }
            .map { it.copyAndDelete() }
            .forEach { ticket.addMessage(it) }

        ticketService.saveTicket(ticket)
    }
}
