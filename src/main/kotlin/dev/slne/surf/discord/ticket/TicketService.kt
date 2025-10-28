package dev.slne.surf.discord.ticket

import dev.slne.surf.discord.command.dsl.embed
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.util.random
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.InteractionHook
import org.springframework.stereotype.Service
import java.awt.Color

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val ticketChannel: TextChannel?,
    private val jda: JDA
) {
    suspend fun createTicket(hook: InteractionHook, type: TicketType): Ticket? {
        val userId = hook.interaction.user.idLong
        val user = hook.interaction.user

        if (hasTicket(userId, type)) {
            return null
        }

        val threadChannel = ticketChannel
            ?.createThreadChannel("Ticket - ${hook.interaction.user.name}", true)
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

        return Ticket(
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
    }

    suspend fun hasTicket(userId: Long, ticketType: TicketType) =
        ticketRepository.getTicket(userId, ticketType) != null

    suspend fun getTicket(userId: Long, ticketType: TicketType): Ticket? {
        TODO("Not yet implemented")
    }

    suspend fun deleteTicket(ticket: Ticket): Boolean {
        TODO("Not yet implemented")
    }
}