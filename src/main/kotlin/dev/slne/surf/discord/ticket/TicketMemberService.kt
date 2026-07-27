package dev.slne.surf.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.ticket.database.members.TicketMemberRepository
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User

object TicketMemberService {
    suspend fun addMember(
        ticket: Ticket,
        user: User,
        addedBy: User,
        welcome: Boolean = true
    ): Boolean {
        if (TicketMemberRepository.isMember(ticket, user.idLong)) {
            return false
        }

        TicketMemberRepository.addMember(
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

        if (welcome) {
            thread.sendMessage(user.asMention).setEmbeds(
                embed {
                    title = "Willkommen im Ticket"
                    description =
                        "Du wurdest zu diesem Ticket hinzugefügt. Bitte sieh dir den Verlauf des Tickets an und warte auf eine Nachricht eines Teammitglieds."
                    color = Colors.WARNING
                    footer = "Hinzugefügt von ${addedBy.name}"
                }
            ).queue()
        }

        return true
    }

    suspend fun addRole(ticket: Ticket, role: Role, addedBy: User): Boolean {
        val guild = ticket.getThreadChannel()?.guild ?: return false
        val members = guild.findMembersWithRoles(role).await() ?: return false

        members.forEach {
            addMember(ticket, it.user, addedBy)
        }

        return true
    }

    suspend fun removeMember(ticket: Ticket, user: User, removedBy: User): Boolean {
        if (!TicketMemberRepository.isMember(ticket, user.idLong)) {
            return false
        }

        TicketMemberRepository.removeMember(
            ticket = ticket,
            removedById = user.idLong,
            removedByName = removedBy.name,
            removedByAvatarUrl = removedBy.avatarUrl
        )

        ticket.getThreadChannel()?.removeThreadMember(user)?.queue()

        return true
    }

    suspend fun isMember(ticket: Ticket, userId: Long) =
        TicketMemberRepository.isMember(ticket, userId)
}