package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.DiscordJdaProvider.jda
import dev.slne.surf.discord.ticket.TicketService
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketArchivingListener(
    private val ticketService: TicketService,
    private val discordScope: CoroutineScope
) : ListenerAdapter() {
    @PostConstruct
    fun init() {
        jda.addEventListener(this)
    }

    override fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {
        val channel = event.channel

        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(channel.idLong) ?: return@launch

            if (!channel.type.isThread) {
                return@launch
            }

            if (ticket.isClosed()) {
                return@launch
            }

            event.channel.asThreadChannel().manager.setArchived(false).queue()
        }
    }
}