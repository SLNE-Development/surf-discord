package dev.slne.discord.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.DiscordBot
import jakarta.persistence.*
import java.io.Serializable
import java.util.*

/**
 * DTO for {@link dev.slne.discord.ticket.Ticket}
 */

open class TicketDto : Serializable {


    suspend fun toTicket(): Ticket {
        val jda = DiscordBot.jda
        val guild = jda.getGuildById(guildId!!) ?: error("Guild not found")
        val author = jda.retrieveUserById(ticketAuthorId!!).await()

        val ticket = Ticket(
            guild,
            author,
            ticketType!!
        )

        ticket.dto = this

        return ticket
    }
}