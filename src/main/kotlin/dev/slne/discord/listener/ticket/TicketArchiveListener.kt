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
import org.springframework.stereotype.Component

@Component
class TicketArchiveListener(private val jda: JDA) {

    @PostConstruct
    fun registerListener() {
        jda.listener<ChannelUpdateArchivedEvent> { event ->
            handleEvent(event.oldValue, event.newValue, event.channel)
        }

        jda.listener<ChannelUpdateLockedEvent> { event ->
            handleEvent(event.oldValue, event.newValue, event.channel)
        }
    }

    suspend fun handleEvent(oldValue: Boolean?, newValue: Boolean?, channel: ChannelUnion) {
        val oldValue = oldValue ?: return
        val newValue = newValue ?: return
        val thread = channel as? ThreadChannel ?: return
        val ticket = thread.ticketOrNull() ?: return

        if (oldValue == newValue) return
        if (!newValue) return
        if (ticket.isClosed) return

        thread.manager.setArchived(false).setLocked(false).await()
    }
}