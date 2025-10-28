package dev.slne.discordold.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discordold.extensions.ticketOrNull
import dev.slne.discordold.persistence.service.ticket.TicketService
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import org.springframework.stereotype.Component

@Component
class MessageDeletedListener(private val jda: JDA, private val ticketService: TicketService) {

    @PostConstruct
    fun registerListener() {
        jda.listener<MessageDeleteEvent> {
            deleteMessage(it.channel, listOf(it.messageId))
        }

        jda.listener<MessageBulkDeleteEvent> { event ->
            deleteMessage(event.channel, event.messageIds)
        }
    }

    private suspend fun deleteMessage(channel: MessageChannel, messageIds: List<String>) {
        val ticket = channel.ticketOrNull() ?: return

        messageIds
            .mapNotNull { ticket.getTicketMessage(it) }
            .map { it.copyAndDelete() }
            .forEach { ticket.addMessage(it) }

        ticketService.saveTicket(ticket)
    }
}
