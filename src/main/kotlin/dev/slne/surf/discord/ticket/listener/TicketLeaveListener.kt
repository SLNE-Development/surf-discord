package dev.slne.surf.discord.ticket.listener

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.logger
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

            val user = runCatching {
                event.threadMember.member
            }.getOrNull() ?: run {
                logger.warn("Failed to check if user is member of ticket ${ticket.ticketId} because member is null")
                return@launch
            }

            if (ticketMemberService.isMember(ticket, user.idLong)) {
                event.thread.sendMessage(user.asMention).setEmbeds(embed {
                    title = "Willkommen zurück!"
                    description =
                        "Du wolltest flüchten - zum Glück habe ich dich an der Leine und konnte dich im Ticket behalten. Bitte habe Geduld, damit wir das Problem gemeinsam lösen können."
                }).queue()
            }
        }
    }
}