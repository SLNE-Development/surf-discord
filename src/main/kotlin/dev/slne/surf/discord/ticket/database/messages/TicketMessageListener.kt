package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.jda
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketMessageListener(
    private val discordScope: CoroutineScope,
    private val ticketService: TicketService,
    private val ticketMessageRepository: TicketMessageRepository,
    private val ticketAttachmentsRepository: TicketAttachmentsRepository
) : ListenerAdapter() {
    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
            ticketMessageRepository.logMessage(ticket, event.message)
            event.message.attachments.forEach {
                ticketAttachmentsRepository.addAttachment(
                    ticket.ticketId,
                    event.message.idLong,
                    it.idLong,
                    it.url,
                    it.proxyUrl
                )
            }
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        discordScope.launch {
            val ticket =
                ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
            ticketMessageRepository.logMessageEdited(event.message.idLong, event.message.contentRaw)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        discordScope.launch {
            val ticket =
                ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
            ticketMessageRepository.logMessageDeleted(event.messageIdLong)
            ticketAttachmentsRepository.markDeleted(ticket.ticketId, event.messageIdLong)
        }
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        discordScope.launch {
            val ticket =
                ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
            for (messageId in event.messageIds.map { it.toLong() }) {
                ticketMessageRepository.logMessageDeleted(messageId)
                ticketAttachmentsRepository.markDeleted(ticket.ticketId, messageId)
            }
        }
    }
}