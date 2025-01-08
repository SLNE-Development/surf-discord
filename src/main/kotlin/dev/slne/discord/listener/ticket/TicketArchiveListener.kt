package dev.slne.discord.listener.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticket
import dev.slne.discord.message.EmbedManager
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.ticket.TicketService
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent

//@Component
class TicketArchiveListener(private val jda: JDA, private val ticketService: TicketService) {

    @PostConstruct
    fun registerListener() {
        jda.listener<ChannelUpdateArchivedEvent> { event ->
            val oldValue = event.oldValue
            val newValue = event.newValue
            val thread = event.channel as? ThreadChannel ?: return@listener
            val ticket = thread.ticket()

            if (oldValue == newValue) return@listener

            if (newValue == true) {
                if (ticket.isClosed || ticket.isClosing) return@listener

                ticket.isClosing = true

                ticket.close(
                    jda.selfUser,
                    translatable("ticket.close.inactive.close-reason")
                )

                thread.sendMessageEmbeds(EmbedManager.buildTicketClosedEmbed(ticket, thread.name))
                    .await()
                thread.manager.setArchived(true).setLocked(true).await()

                ticketService.saveTicket(ticket)
                ticket.isClosing = false
            } else if (newValue == false) {
                if (!ticket.isClosed || ticket.isClosing) return@listener

                ticket.reopen()
                ticketService.saveTicket(ticket)

                thread.sendMessageEmbeds(EmbedManager.buildTicketReopenEmbed(ticket)).await()
            }
        }
    }
}