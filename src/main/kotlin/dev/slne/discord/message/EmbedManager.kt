package dev.slne.discord.message

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.message.EmbedColors.TICKET_CLOSED
import dev.slne.discord.ticket.Ticket

object EmbedManager {

    suspend fun buildTicketClosedEmbed(ticket: Ticket) =
        Embed { // TODO: 14.10.2024 23:18 - use messages
            title = "Ticket \"${ticket.thread?.name ?: "Unbekannt"}\" geschlossen"

            description = buildString {
                append("Ein Ticket wurde von ")
                ticket.closedBy?.await()?.let { append(it.asMention) }
                append(" geschlossen.\n\n")
                append("Grund: ${ticket.closeReasonOrDefault}")
            }

            color = TICKET_CLOSED

            field {
                name = "Ticket-Id"
                value = ticket.ticketId.toString()
            }

            field {
                name = "Ticket-Type"
                value = ticket.ticketType.displayName
            }

            field {
                name = "Ticket-Author"
                value = ticket.ticketAuthor?.await()?.asMention ?: "Unbekannt"
            }

            field {
                name = "Ticket-Eröffnungszeit"
                value = ticket.openedAt.formatEuropeBerlin()
            }

            field {
                name = "Ticket-Schließzeit"
                value = ticket.closedAt!!.formatEuropeBerlin()
            }

            field {
                name = "Ticket-Dauer"
                value = formatDuration(ticket.openedAt, ticket.closedAt!!)
            }
        }
}
