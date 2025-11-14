package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.TicketMemberService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "add",
    description = "Füge einen Nutzer zum Ticket hinzu",
    options = [
        CommandOption(
            name = "user",
            description = "Der hinzuzufügende Nutzer",
            type = CommandOptionType.USER,
            required = true
        )
    ]
)
@Component
class TicketAddUserCommand(
    private val ticketMemberService: TicketMemberService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val user = event.getOption("user")?.asUser ?: error("User option is missing")
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply(translatable("ticket.command.not-a-ticket"))
                .setEphemeral(true).queue()
            return
        }

        val success = ticketMemberService.addMember(ticket, user, event.user)

        if (success) {
            event.reply(translatable("ticket.command.add.success", user.asMention))
                .setEphemeral(true)
                .queue()
        } else {
            event.reply(translatable("ticket.command.add.already-member", user.asMention))
                .setEphemeral(true)
                .queue()
        }
    }
}