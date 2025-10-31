package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.TicketMemberService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    "add", "Füge einen Nutzer zum Ticket hinzu", options = [
        CommandOption("user", "Der hinzuzufügende Nutzer", CommandOptionType.USER, true)
    ]
)
@Component
class TicketAddUserCommand(
    private val ticketMemberService: TicketMemberService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD)) {
            event.reply("Dazu hast du keine Berechtigung.").setEphemeral(true).queue()
            return
        }

        val user = event.getOption("user")?.asUser ?: error("User option is missing")
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply("Du musst dich in einem Ticket befinden, um diesen Befehl zu nutzen.")
                .setEphemeral(true).queue()
            return
        }

        if (ticketMemberService.isMember(ticket, user.idLong)) {
            event.reply("${user.asMention} ist bereits Mitglied dieses Tickets.").setEphemeral(true)
                .queue()
            return
        }

        val success = ticketMemberService.addMember(ticket, user, event.user)

        if (success) {
            event.reply("${user.asMention} wurde zum Ticket hinzugefügt.").setEphemeral(true)
                .queue()
        } else {
            event.reply("${user.asMention} ist bereits in diesem Ticket.").setEphemeral(true)
                .queue()
        }
    }
}