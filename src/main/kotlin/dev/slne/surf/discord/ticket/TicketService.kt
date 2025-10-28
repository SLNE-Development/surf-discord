package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.util.random
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service
import java.awt.Color

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val ticketChannel: TextChannel?
) {
    suspend fun createTicket(hook: InteractionHook, type: TicketType): Ticket? {
        val userId = hook.interaction.user.idLong
        val user = hook.interaction.user

        if (hasTicket(userId, type)) {
            return null
        }

        val threadChannel = ticketChannel
            ?.createThreadChannel("${type.id}-${hook.interaction.user.name}", true)
            ?.setInvitable(false)
            ?.complete(true) ?: run {
            hook.editOriginalEmbeds(embed {
                title = "Ticket Erstellung Fehlgeschlagen"
                description =
                    "Es ist ein unbekannter Fehler aufgetreten. Bitte versuche es später erneut. Sollte dieses Problem weiterhin bestehen, kontaktiere ein Teammitglied."
                color = Color.RED
            }).queue()
            return null
        }
        
        threadChannel.addThreadMember(user)

        val ticket = Ticket(
            random.nextLong(),
            null,
            userId,
            user.name,
            user.avatarUrl,
            threadChannel.guild.idLong,
            threadChannel.idLong,
            type,
            System.currentTimeMillis(),
            null,
            null,
            null,
            null,
            null
        )

        ticketRepository.createTicket(ticket)

        return ticket
    }

    suspend fun updateData(ticket: Ticket, ticketData: String) =
        ticketRepository.updateData(ticket, ticketData)

    suspend fun getTicketByThreadId(threadId: Long) =
        ticketRepository.getTicketByThreadId(threadId)

    suspend fun hasTicket(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType) != null

    suspend fun getTicketByUserAndType(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType)

    suspend fun closeTicket(ticket: Ticket, reason: String, closer: User) {
        val thread = ticket.getThreadChannel() ?: return

        thread.sendMessageEmbeds(
            embed {
                title = "Ticket Geschlossen"
                description = """
                    Das Ticket wurde von ${closer.asMention} geschlossen.
                    
                    **Grund:** $reason
                """.trimIndent()
                color = Color.RED
            }
        ).queue()

        thread.manager.setLocked(true).queue()
        thread.manager.setArchived(true).queue()
        ticket.closedAt = System.currentTimeMillis()
        ticket.closedById = closer.idLong
        ticket.closedByName = closer.name
        ticket.closedByAvatar = closer.avatarUrl
        ticket.closedReason = reason

        deleteTicket(ticket)
    }

    suspend fun closeTicket(hook: InteractionHook, reason: String) {
        val ticket = getTicketByThreadId(hook.interaction.channel?.idLong ?: 0L) ?: return

        closeTicket(ticket, reason, hook.interaction.user)
        deleteTicket(ticket)
    }

    suspend fun deleteTicket(ticket: Ticket) = ticketRepository.deleteTicket(ticket.ticketId)
}