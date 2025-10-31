package dev.slne.surf.discord.ticket.database.messages

import dev.slne.surf.discord.jda
import dev.slne.surf.discord.ticket.TicketService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketMessageListener(
    private val discordScope: CoroutineScope,
    private val ticketService: TicketService,
    private val ticketMessageRepository: TicketMessageRepository
) : ListenerAdapter() {
    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(event.channel.idLong) ?: return@launch
            ticketMessageRepository.logMessage(ticket, event.message)
        }
    }
}