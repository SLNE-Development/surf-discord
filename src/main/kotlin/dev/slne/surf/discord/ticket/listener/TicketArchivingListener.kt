package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.logger
import dev.slne.surf.discord.ticket.TicketService
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
    override fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {
        val channel = event.channel

        logger.info("Channel ${channel.name} archiving status changed to ${event.newValue}")

        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(channel.idLong)
                ?: return@launch

            if (!channel.type.isThread) {
                logger.info("Channel ${channel.name} is not a thread, ignoring.")
                return@launch
            }

            if (ticket.isClosed()) {
                logger.info("Ticket ${ticket.ticketId} is closed, allowing archiving of thread channel ${channel.name}.")
                return@launch
            }

            logger.info("Ticket ${ticket.ticketId} is not closed, preventing archiving of thread channel ${channel.name}.")

            event.channel.asThreadChannel().manager.setArchived(false).queue()

            logger.info("Prevented archiving of ticket thread channel ${channel.name} because the ticket is not closed.")
        }
    }
}