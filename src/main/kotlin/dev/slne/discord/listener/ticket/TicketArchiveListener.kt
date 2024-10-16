package dev.slne.discord.listener.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.DiscordBot
import dev.slne.discord.extensions.ticket
import dev.slne.discord.message.EmbedManager
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.ticket.TicketRepository
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.kyori.adventure.text.logger.slf4j.ComponentLogger

object TicketArchiveListener {

    private val logger = ComponentLogger.logger()

    init {
        DiscordBot.jda.listener<ChannelUpdateArchivedEvent> { event ->
            val oldValue = event.oldValue
            val newValue = event.newValue
            val thread = event.channel as ThreadChannel
            val ticket = thread.ticket
                ?: TicketRepository.findByThreadId(event.channel.id)
                ?: return@listener

            if (newValue == true && !ticket.isClosed && !ticket.isClosing) {
                ticket.isClosing = true

                ticket.close(
                    DiscordBot.jda.selfUser,
                    translatable("ticket.close.inactive.close-reason")
                )

                thread.sendMessageEmbeds(EmbedManager.buildTicketClosedEmbed(ticket, thread.name))
                    .await()
                thread.manager.setArchived(true).setLocked(true).await()

                ticket.save()

                ticket.isClosing = false
            } else if (newValue == false && ticket.isClosed && !ticket.isClosing) {
                ticket.reopen()
                ticket.save()

                thread.sendMessageEmbeds(EmbedManager.buildTicketReopenEmbed(ticket)).await()
            }
        }
    }
}