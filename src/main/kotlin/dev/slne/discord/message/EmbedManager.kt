package dev.slne.discord.message

import dev.slne.discord.exception.ticket.UnableToGetTicketNameException
import dev.slne.discord.message.EmbedColors.TICKET_CLOSED
import dev.slne.discord.ticket.Ticket
import dev.slne.discord.ticket.TicketChannelHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmbedManager(private val ticketChannelHelper: TicketChannelHelper) {

    suspend fun buildTicketClosedEmbed(ticket: Ticket): MessageEmbed? {
        try {
            val ticketName: String = ticketChannelHelper.getTicketName(ticket).join()
            val author = withContext(Dispatchers.IO) { ticket.ticketAuthor?.complete() }
            val closedBy = withContext(Dispatchers.IO) { ticket.closedBy?.complete() }
            val closeReason = ticket.closeReasonOrDefault

            val description = buildString {
                append("Ein Ticket wurde von ")
                closedBy?.let { append(it.asMention) }
                append(" geschlossen.\n\n")
                append("Grund: $closeReason")
            }

            return EmbedBuilder()
                .setTitle("Ticket \"%s\" geschlossen".formatted(ticketName))
                .setDescription(description)
                .setColor(TICKET_CLOSED)
                .addField("Ticket-Id", toStringOrUnknown(ticket.ticketId), true)
                .addField("Ticket-Type", toStringOrUnknown(ticket.ticketTypeString), true)
                .addField(
                    "Ticket-Author",
                    toStringOrUnknown(author.map<String> { it.asMention }),
                    true
                )
                .addField(
                    "Ticket-Eröffnungszeit",
                    ticket.getOpenedAt().formatEuropeBerlin(),
                    true
                )
                .addField("Ticket-Schließzeit", ticket.getClosedAt().formatEuropeBerlin(), true)
                .addField(
                    "Ticket-Dauer",
                    ticket.getOpenedAt().formatEuropeBerlinDuration(ticket.getClosedAt()), true
                )
                .build()
        } catch (e: UnableToGetTicketNameException) {
            return null
        }
    }

    private fun toStringOrUnknown(`object`: Any?): String {
        return `object`?.toString() ?: "Unbekannt"
    }

    private fun toStringOrUnknown(optional: Optional<*>): String {
        return optional.map { obj: Any -> obj.toString() }.orElse("Unbekannt")
    }
}
