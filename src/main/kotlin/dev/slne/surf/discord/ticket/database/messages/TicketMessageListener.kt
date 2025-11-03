package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsRepository
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
    override fun onMessageReceived(event: MessageReceivedEvent) {
        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(event.channel.idLong)
                ?: return@launch

            ticketMessageRepository.logMessage(ticket, event.message)

            event.message.attachments.forEach {
                ticketAttachmentsRepository.addAttachment(
                    ticket.ticketUid,
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
            if (!ticketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            ticketMessageRepository.logMessageEdited(event.message.idLong, event.message.contentRaw)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        discordScope.launch {
            if (!ticketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            ticketMessageRepository.logMessageDeleted(event.messageIdLong)
            
            // TODO: Just delete this, as we dont want to store user images serverside and the attachments are gone anyway
            ticketAttachmentsRepository.markDeleted(event.messageIdLong)
        }
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        discordScope.launch {
            if (!ticketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            for (messageId in event.messageIds.map { it.toLong() }) {
                ticketMessageRepository.logMessageDeleted(messageId)
                
            // TODO: Just delete this, as we dont want to store user images serverside and the attachments are gone anyway
                ticketAttachmentsRepository.markDeleted(messageId)
            }
        }
    }
}