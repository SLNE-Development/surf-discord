package dev.slne.surf.discord.ticket.listener

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.ticket.TicketMemberService
import dev.slne.surf.discord.ticket.TicketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.ErrorResponse
import org.springframework.stereotype.Component

@Component
class TicketLeaveListener(
    private val discordScope: CoroutineScope,
    private val ticketService: TicketService,
    private val ticketMemberService: TicketMemberService
) : ListenerAdapter() {
    override fun onThreadMemberLeave(event: ThreadMemberLeaveEvent) {
        discordScope.launch {
            val thread = event.thread
            val userId = event.threadMemberIdLong

            val ticket = ticketService.getTicketByThreadId(thread.idLong) ?: return@launch

            if (!ticketMemberService.isMember(ticket, userId)) {
                return@launch
            }

            val member = try {
                event.guild.retrieveMemberById(userId).await()
            } catch (e: ErrorResponseException) {
                when (e.errorResponse) {
                    ErrorResponse.UNKNOWN_MEMBER,
                    ErrorResponse.UNKNOWN_USER -> null

                    else -> throw e
                }
            }

            if (member != null) {
                thread.sendMessage("<@$userId>")
                    .setEmbeds(embed {
                        title = "Willkommen zurück!"
                        description = """
                            Du wolltest flüchten – zum Glück habe ich dich an der Leine und konnte dich im Ticket behalten.
                            Bitte habe Geduld, damit wir das Problem gemeinsam lösen können.
                        """.trimIndent()
                    })
                    .await()
            } else {
                thread.sendMessage("<@$userId>")
                    .setEmbeds(embed {
                        title = "Discord verlassen!"
                        description = "Der Benutzer <@$userId> hat den Discord-Server verlassen."
                    })
                    .await()
            }
        }
    }
}