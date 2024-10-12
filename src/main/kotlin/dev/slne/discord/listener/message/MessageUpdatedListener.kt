package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import net.dv8tion.jda.api.events.message.MessageUpdateEvent

object MessageUpdatedListener : AbstractMessageListener() {

    init {
        DiscordBot.jda.listener<MessageUpdateEvent> { event ->
            val ticket = getTicketByChannel(event.channel) ?: return@listener

            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticketMessage = ticket.getTicketMessage(event.messageId) ?: return@listener

            ticketMessage.update(event.message).let {
                ticket.addRawTicketMessage(it)
            }
        }
    }
}
