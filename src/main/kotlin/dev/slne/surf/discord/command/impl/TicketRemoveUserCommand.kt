package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.ticket.TicketMemberService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    "remove", "Entferne einen Nutzer vom Ticket", options = [
        CommandOption("user", "Der Nutzer zum entfernen", CommandOptionType.USER, true)
    ]
)
@Component
class TicketRemoveUserCommand(
    private val ticketMemberService: TicketMemberService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("user")?.asUser ?: error("User option is missing")
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply("Du musst dich in einem Ticket befinden, um diesen Befehl zu nutzen.")
                .setEphemeral(true).queue()
            return
        }

        val success = ticketMemberService.removeMember(ticket, user, event.user)

        if (success) {
            event.reply("${user.asMention} wurde aus dem Ticket entfernt.").setEphemeral(true)
                .queue()
        } else {
            event.reply("${user.asMention} ist nicht in diesem Ticket.").setEphemeral(true).queue()
        }
    }
}