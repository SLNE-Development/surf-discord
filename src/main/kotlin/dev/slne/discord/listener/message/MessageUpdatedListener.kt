package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.extensions.ticket
import net.dv8tion.jda.api.events.message.MessageUpdateEvent

object MessageUpdatedListener {

    init {
        DiscordBot.jda.listener<MessageUpdateEvent> { event ->
            val ticket = event.channel.ticket ?: return@listener

            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticketMessage = ticket.getTicketMessage(event.messageId) ?: return@listener
            ticketMessage.update(event.message)
        }
    }
}
