package dev.slne.discord.listener.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.listener
import dev.slne.discord.extensions.ticketOrNull
import jakarta.annotation.PostConstruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateLockedEvent

class TicketKeepActiveListener(private val jda: JDA) {

    @PostConstruct
    fun registerListener() {
        jda.listener<ChannelUpdateArchivedEvent> { event ->
            val thread = event.channel as? ThreadChannel ?: return@listener
            val ticket = thread.ticketOrNull() ?: return@listener

            if (event.newValue == true && ticket.isClosed == false) {
                thread.manager.setArchived(false).queue()
            }
        }
    }
}