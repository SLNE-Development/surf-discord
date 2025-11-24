package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.ticket.database.members.TicketMemberRepository
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service

@Service
class TicketMemberService(
    private val ticketMemberRepository: TicketMemberRepository
) {
    suspend fun addMember(ticket: Ticket, user: User, addedBy: User): Boolean {
        if (ticketMemberRepository.isMember(ticket, user.idLong)) {
            return false
        }

        ticketMemberRepository.addMember(
            ticket = ticket,
            userId = user.idLong,
            userName = user.name,
            userAvatarUrl = user.avatarUrl,
            addedById = addedBy.idLong,
            addedByName = addedBy.name,
            addedByAvatarUrl = addedBy.avatarUrl
        )

        val thread = ticket.getThreadChannel() ?: return false

        thread.addThreadMember(user).queue()
        thread.sendMessage(user.asMention).setEmbeds(
            embed {
                title = "Willkommen im Ticket"
                description =
                    "Du wurdest zu diesem Ticket hinzugefügt. Bitte sieh dir den Verlauf des Tickets an und warte auf eine Nachricht eines Teammitglieds."
                color = Colors.WARNING
                footer = "Hinzugefügt von ${addedBy.name}"
            }
        ).queue()

        return true
    }

    suspend fun removeMember(ticket: Ticket, user: User, removedBy: User): Boolean {
        if (!ticketMemberRepository.isMember(ticket, user.idLong)) {
            return false
        }

        ticketMemberRepository.removeMember(
            ticket = ticket,
            removedById = user.idLong,
            removedByName = removedBy.name,
            removedByAvatarUrl = removedBy.avatarUrl
        )

        ticket.getThreadChannel()?.removeThreadMember(user)?.queue()

        return true
    }

    suspend fun isMember(ticket: Ticket, userId: Long) =
        ticketMemberRepository.isMember(ticket, userId)
}