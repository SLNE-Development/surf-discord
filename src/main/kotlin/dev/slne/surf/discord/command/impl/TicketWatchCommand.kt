package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.ticket.TicketService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand("watch", "Beobachte das Ticket.")
class TicketWatchCommand(
    private val ticketService: TicketService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply("Du musst dich in einem Ticket befinden, um diesen Befehl zu nutzen.")
                .setEphemeral(true).queue()
            return
        }

        val claimed = ticketService.isWatched(ticket)

        if (claimed) {
            ticketService.unwatch(ticket)
            event.reply("Du beobachtest das Ticket nun nicht mehr.").setEphemeral(true).queue()
        } else {
            ticketService.watch(ticket, event.user)
            event.reply("Du beobachtest nun das Ticket.").setEphemeral(true).queue()
        }
    }
}