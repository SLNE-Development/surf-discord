package dev.slne.surf.discord.ticket.command.context

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

private const val PANEL_BASE_URL = "https://support.castcrafter.de/discord/tickets"

@DiscordCommand(
    name = "mytickets",
    description = "Zeigt alle offenen Tickets, die du selbst übernommen hast."
)
@Component
class MyTicketsCommand(
    private val ticketRepository: TicketRepository
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_CLAIM)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val tickets = ticketRepository.getOpenTicketsClaimedBy(event.user.idLong)
            .filter { it.threadId != null }
            .sortedByDescending { it.createdAt }

        val text = if (tickets.isEmpty()) {
            translatable("ticket.command.mytickets.empty")
        } else {
            tickets.mapIndexed { index, ticket ->
                val threadUrl = "https://discord.com/channels/${ticket.guildId}/${ticket.threadId}"
                val panelUrl = "$PANEL_BASE_URL/${ticket.ticketId}"
                "#${index + 1} <$threadUrl> |--| <$panelUrl>"
            }.joinToString("\n")
        }

        event.reply(text).setEphemeral(true).queue()
    }
}
