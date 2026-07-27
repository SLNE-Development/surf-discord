package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.ticket.database.messages.attachments.TicketAttachmentsRepository
import dev.slne.surf.discord.ticket.deadline.ReplyDeadlineService
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object TicketMessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        discordScope.launch {
            val ticket = TicketService.getTicketByThreadId(event.channel.idLong)
                ?: return@launch

            if (!event.author.isBot) {
                ReplyDeadlineService.onUserReplied(event.channel.idLong, event.author.idLong)
            }

            val dbMessageId = TicketMessageRepository.logMessage(ticket, event.message)

            event.message.attachments.forEach {
                TicketAttachmentsRepository.addAttachment(
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
            if (!TicketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            TicketMessageRepository.logMessageEdited(event.message.idLong, event.message.contentRaw)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        discordScope.launch {
            if (!TicketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            TicketMessageRepository.logMessageDeleted(event.messageIdLong)

            val dbMessageId =
                TicketMessageRepository.getDbIdFromDiscordMessageId(event.messageIdLong)
            if (dbMessageId != null) {
                TicketAttachmentsRepository.delete(dbMessageId)
            }
        }
    }

    override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
        discordScope.launch {
            if (!TicketService.isTicketExisting(event.channel.idLong)) {
                return@launch
            }

            for (messageId in event.messageIds.map { it.toLong() }) {
                TicketMessageRepository.logMessageDeleted(messageId)

                val dbMessageId = TicketMessageRepository.getDbIdFromDiscordMessageId(messageId)
                if (dbMessageId != null) {
                    TicketAttachmentsRepository.delete(dbMessageId)
                }
            }
        }
    }
}