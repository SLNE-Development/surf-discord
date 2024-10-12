package dev.slne.discord.listener.message

import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.ticket.message.TicketMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object MessageCreatedListener : AbstractMessageListener() {

    init {
        DiscordBot.jda.listener<MessageReceivedEvent> { event ->
            if (event.message.isWebhookMessage) {
                return@listener
            }

            val ticket = getTicketByChannel(event.channel)

            ticket?.addTicketMessage(
                TicketMessage.fromTicketAndMessage(
                    ticket,
                    event.message
                )
            )
        }
    }
}
