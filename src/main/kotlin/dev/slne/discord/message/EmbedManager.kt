package dev.slne.discord.message

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.message.EmbedColors.TICKET_CLOSED
import dev.slne.discord.message.EmbedColors.TICKET_REOPENED
import dev.slne.discord.ticket.Ticket

object EmbedManager {

    fun buildTicketClosedUserPrivateMessageEmbed(
        ticket: Ticket,
        threadName: String? = ticket.thread?.name
    ) =
        Embed { // TODO: 14.10.2024 23:18 - use messages
            title = "Ticket \"${threadName ?: "Unbekannt"}\" geschlossen"

            description = buildString {
                append("Dein Ticket wurde geschlossen.\n\n")
                append("Grund: ${ticket.closeReasonOrDefault}\n\n")
                append("Weitere Informationen findest du im Ticket.\n${ticket.thread?.asMention}")
            }

            color = TICKET_CLOSED
        }

    suspend fun buildTicketClosedEmbed(ticket: Ticket, threadName: String? = ticket.thread?.name) =
        Embed { // TODO: 14.10.2024 23:18 - use messages
            title = "Ticket \"${threadName ?: "Unbekannt"}\" geschlossen"

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
                value = ticket.ticketType!!.displayName
            }

            field {
                name = "Ticket-Author"
                value = ticket.author?.await()?.asMention ?: "Unbekannt"
            }

            field {
                name = "Ticket-Eröffnungszeit"
                value = ticket.openedAt.format()
            }

            field {
                name = "Ticket-Schließzeit"
                value = ticket.closedAt.format()
            }

            field {
                name = "Ticket-Dauer"
                value = formatDuration(ticket.openedAt, ticket.closedAt)
            }
        }

    fun buildTicketReopenEmbed(ticket: Ticket) =
        Embed { // TODO: 14.10.2024 23:18 - use messages
            title = "Ticket \"${ticket.thread?.name ?: "Unbekannt"}\" wiedereröffnet"
            color = TICKET_REOPENED
        }
}
