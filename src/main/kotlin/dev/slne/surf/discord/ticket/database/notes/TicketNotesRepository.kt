package dev.slne.surf.discord.ticket.database.notes

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.discord.ticket.Ticket
import net.dv8tion.jda.api.entities.Member

object TicketNotesRepository {
    suspend fun addNote(ticket: Ticket, author: Member, content: String) = suspendTransaction {
        TicketNotesTable.insert {
            it[ticketId] = ticket.ticketId
            it[authorId] = author.idLong
            it[authorName] = author.effectiveName
            it[authorAvatarUrl] = author.user.avatarUrl
            it[this.content] = content
        }
    }
}