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
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@DiscordCommand(
    name = "add",
    description = "Füge einen Nutzer oder eine Rolle zum Ticket hinzu",
    options = [
        CommandOption(
            name = "user",
            description = "Der hinzuzufügende Nutzer",
            type = CommandOptionType.USER,
            required = false
        ),
        CommandOption(
            name = "role",
            description = "Die hinzuzufügende Rolle",
            type = CommandOptionType.ROLE,
            required = false
        ),
        CommandOption(
            name = "silent",
            description = "Fügt den Nutzer/die Rolle still hinzu, ohne eine Nachricht zu senden",
            type = CommandOptionType.BOOLEAN,
            required = false
        )
    ]
)
object TicketAddEntityCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val user = event.getOption("user")?.asUser
        val role = event.getOption("role")?.asRole
        val silent = event.getOption("silent")?.asBoolean ?: false
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply(translatable("ticket.command.not-a-ticket"))
                .setEphemeral(true).queue()
            return
        }

        val targetRole = if (user == null) role else null

        if (user == null && targetRole == null) {
            event.reply(translatable("ticket.command.add.missing-target"))
                .setEphemeral(true)
                .queue()
            return
        }

        if (silent && !event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD_SILENT)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        if (silent) {
            val mention = user?.asMention ?: targetRole?.asMention
            ?: error("Target user and role are both null")

            val msg = event.channel.sendMessage("silent").submit(true).await()
            val edited = msg.editMessage(mention).submit(true).await()
            edited.delete().queue()

            event.reply(translatable("ticket.command.add.success", mention))
                .setEphemeral(true)
                .queue()
            return
        }

        val success = if (user != null) {
            TicketMemberService.addMember(ticket, user, event.user)
        } else {
            if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD_ROLE)) {
                event.reply(translatable("no-permission")).setEphemeral(true).queue()
                return
            }

            TicketMemberService.addRole(
                ticket,
                targetRole ?: error("Target user and role are both null"),
                event.user
            )
        }

        val mention = user?.asMention ?: targetRole?.asMention
        ?: error("Target user and role are both null")

        if (success) {
            event.reply(translatable("ticket.command.add.success", mention))
                .setEphemeral(true)
                .queue()
        } else {
            event.reply(translatable("ticket.command.add.already-member", mention))
                .setEphemeral(true)
                .queue()
        }
    }
}
