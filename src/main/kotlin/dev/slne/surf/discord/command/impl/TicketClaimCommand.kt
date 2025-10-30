package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand("claim", "Bearbeite das Ticket.")
class TicketClaimCommand(
    private val ticketService: TicketService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val ticket = event.hook.asTicketOrNull()

        if(ticket == null) {
            event.reply("Du musst dich in einem Ticket befinden, um diesen Befehl zu nutzen.")
                .setEphemeral(true).queue()
            return
        }

        val claimed = ticketService.isClaimed(ticket)

        if(claimed) {
            ticketService.unclaim(ticket)
            event.reply("Das Ticket wurde freigegeben.").setEphemeral(true).queue()
        } else {
            ticketService.claim(ticket, event.user)
            event.reply("Du bearbeitest nun das Ticket.").setEphemeral(true).queue()
        }
    }
}