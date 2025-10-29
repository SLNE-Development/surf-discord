package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.dsl.embed
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

        if (hasOpenTicket(userId, type)) {
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

        threadChannel.addThreadMember(user).queue()

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

    suspend fun hasOpenTicket(userId: Long, ticketType: TicketType) =
        ticketRepository.hasOpenTicket(userId, ticketType)

    suspend fun getTicketByUserAndType(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType)

    suspend fun closeTicket(ticket: Ticket, reason: String, closer: User) {
        val thread = ticket.getThreadChannel() ?: return

        thread.sendMessageEmbeds(
            embed {
                title = "Ticket Geschlossen"
                description =
                    "Das Ticket wurde von ${closer.asMention} geschlossen. \n \nGrund: $reason"
                color = Color.RED

                field {
                    name = "Ticket Typ"
                    value = ticket.ticketType.displayName
                    inline = true
                }

                field {
                    name = "Ticket Id"
                    value = ticket.ticketId.toString()
                    inline = true
                }

                field {
                    name = "Ticket Author"
                    value = "<@${ticket.authorId}>"
                    inline = true

                }

                field {
                    name = "Ticket Erstellungsdatum"
                    value = "<t:${ticket.createdAt / 1000}:F>"
                    inline = true
                }

                field {
                    name = "Ticket Schließungsdatum"
                    value = "<t:${System.currentTimeMillis() / 1000}:F>"
                    inline = true
                }

                field {
                    name = "Ticket Dauer"
                    value =
                        "<t:${ticket.createdAt / 1000}:R> bis <t:${System.currentTimeMillis() / 1000}:R>"
                }
            }
        ).queue()

        thread.manager.setLocked(true).queue()
        thread.manager.setArchived(true).queue()
        ticket.closedAt = System.currentTimeMillis()
        ticket.closedById = closer.idLong
        ticket.closedByName = closer.name
        ticket.closedByAvatar = closer.avatarUrl
        ticket.closedReason = reason

        markAsClosed(ticket)
    }

    suspend fun closeTicket(hook: InteractionHook, reason: String) {
        val ticket = getTicketByThreadId(hook.interaction.channel?.idLong ?: 0L) ?: return

        closeTicket(ticket, reason, hook.interaction.user)
    }

    suspend fun markAsClosed(
        ticket: Ticket
    ) =
        ticketRepository.markAsClosed(
            ticket
        )
}