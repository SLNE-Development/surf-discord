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

            val dbMessageId = ticketMessageRepository.logMessage(ticket, event.message)

            event.message.attachments.forEach {
                ticketAttachmentsRepository.addAttachment(
                    it.idLong,
                    it.fileName,
                    it.url,
                    it.proxyUrl,
                    it.waveform.toString(),
                    it.contentType,
                    it.description,
                    it.size,
                    it.height,
                    it.width,
                    it.isEphemeral,
                    it.duration.toFloat(),
                    dbMessageId,
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
            
            // Get the database ID from the Discord message ID before deleting attachments
            val dbMessageId = ticketMessageRepository.getDbIdFromDiscordMessageId(event.messageIdLong)
            if (dbMessageId != null) {
                ticketAttachmentsRepository.delete(dbMessageId)
            }
        }
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        discordScope.launch {
            if (!ticketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            for (messageId in event.messageIds.map { it.toLong() }) {
                ticketMessageRepository.logMessageDeleted(messageId)
                
                // Get the database ID from the Discord message ID before deleting attachments
                val dbMessageId = ticketMessageRepository.getDbIdFromDiscordMessageId(messageId)
                if (dbMessageId != null) {
                    ticketAttachmentsRepository.delete(dbMessageId)
                }
            }
        }
    }
}