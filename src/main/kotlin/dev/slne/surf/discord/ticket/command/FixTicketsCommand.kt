package dev.slne.surf.discord.ticket.command

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "ticket-fix",
    description = "Öffne archivierte Tickets, welche in der Datenbank noch offen sind."
)
@Component
class FixTicketsCommand(
    private val ticketRepository: TicketRepository,
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_FIX)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val openTickets = ticketRepository.getOpenTickets()
        var fixedAmount = 0

        openTickets.forEach { ticket ->
            val channel = ticket.getThreadChannel() ?: return@forEach
            if (!channel.isArchived) return@forEach

            val rest = channel.sendMessage(".").await()
            rest.delete().await()

            fixedAmount++
        }

        event.reply(translatable("ticket.fix.done", fixedAmount.toString()))
            .setEphemeral(true)
            .queue()
    }
}
