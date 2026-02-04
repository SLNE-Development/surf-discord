package dev.slne.surf.discord.ticket.listener

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.ticket.TicketMemberService
import dev.slne.surf.discord.ticket.TicketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class TicketLeaveListener(
    private val discordScope: CoroutineScope,
    private val ticketService: TicketService,
    private val ticketMemberService: TicketMemberService
) : ListenerAdapter() {
    override fun onThreadMemberLeave(event: ThreadMemberLeaveEvent) {
        discordScope.launch {
            val ticket = ticketService.getTicketByThreadId(event.thread.idLong) ?: return@launch

            if (ticketMemberService.isMember(ticket, event.threadMember.member.idLong)) {
                val message =
                    event.thread.sendMessage("Readding ${event.threadMember.member.nickname}")
                        .await()
                val edited = message.editMessage(event.threadMember.member.asMention).await()
                edited.delete().queue()
            }
        }
    }
}