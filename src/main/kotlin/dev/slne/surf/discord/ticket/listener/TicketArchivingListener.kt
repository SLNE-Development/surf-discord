package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.discordScope
import dev.slne.surf.discord.ticket.TicketService
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object TicketArchivingListener : ListenerAdapter() {
    override fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {
        val channel = event.channel

        discordScope.launch {
            val ticket = TicketService.getTicketByThreadId(channel.idLong)
                ?: return@launch

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