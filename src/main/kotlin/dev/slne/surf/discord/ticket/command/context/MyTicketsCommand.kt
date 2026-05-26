package dev.slne.surf.discord.ticket.command.context

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.ticket.TicketRepository
import dev.slne.surf.discord.util.Colors
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

        if (tickets.isEmpty()) {
            event.replyEmbeds(embed {
                title = translatable("ticket.command.mytickets.title")
                description = translatable("ticket.command.mytickets.empty")
                color = Colors.INFO
            }).setEphemeral(true).queue()
            return
        }

        event.replyEmbeds(embed {
            title = translatable("ticket.command.mytickets.title")
            color = Colors.PRIMARY
            footer = translatable("ticket.command.mytickets.footer", tickets.size.toString())

            description = tickets.mapIndexed { index, ticket ->
                val panelUrl = "$PANEL_BASE_URL/${ticket.ticketId}"
                "`#${index + 1}` <#${ticket.threadId}> → [Im Panel ansehen]($panelUrl)"
            }.joinToString("\n")
        }).setEphemeral(true).queue()
    }
}