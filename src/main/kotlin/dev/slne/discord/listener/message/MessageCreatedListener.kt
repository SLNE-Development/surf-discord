package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.extensions.ticket
import dev.slne.discord.ticket.message.toTicketMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object MessageCreatedListener {

    init {
        DiscordBot.jda.listener<MessageReceivedEvent> { event ->
            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticket = event.channel.ticket ?: return@listener

            val message = event.message.toTicketMessage()
            ticket.addMessage(message)
            message.create()
        }
    }
}
