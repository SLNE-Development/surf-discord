package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
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
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_WATCH)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply(translatable("ticket.command.not-a-ticket"))
                .setEphemeral(true).queue()
            return
        }

        val claimed = ticketService.isWatchedByUser(ticket, event.user)

        if (claimed) {
            ticketService.unwatch(ticket, event.user)
            event.reply(translatable("ticket.command.watch.unwatched")).setEphemeral(true).queue()
        } else {
            if (ticketService.isWatched(ticket)) {
                event.reply(translatable("ticket.command.watch.already-watched"))
                    .setEphemeral(true).queue()
                return
            }

            ticketService.watch(ticket, event.user)
            event.reply(translatable("ticket.command.watch.watched")).setEphemeral(true).queue()
        }
    }
}