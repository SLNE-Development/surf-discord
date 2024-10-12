package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.extensions.ticket
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent

object MessageDeletedListener {

    init {
        DiscordBot.jda.listener<MessageDeleteEvent> {
            deleteMessage(it.channel, listOf(it.messageId))
        }

        DiscordBot.jda.listener<MessageBulkDeleteEvent> {
            deleteMessage(it.channel, it.messageIds)
        }
    }

    private suspend fun deleteMessage(channel: MessageChannel, messageIds: List<String>) {
        val ticket = channel.ticket ?: return

        messageIds
            .mapNotNull { ticket.getTicketMessage(it) }
            .mapNotNull { it.delete() }
            .forEach { ticket.addRawTicketMessage(it) }
    }
}
