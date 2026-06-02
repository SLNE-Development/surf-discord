package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.deadline.ReplyDeadlineService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@DiscordCommand(
    "reply-deadline", "Sende eine Reply-Deadline in ein Ticket.", options = [
        CommandOption(
            name = "user",
            description = "Der Nutzer, für den die Reply-Deadline gesetzt wird.",
            type = CommandOptionType.USER,
            required = true
        ),
        CommandOption(
            name = "until",
            description = "Setze eine eigene Zeit in Stunden",
            type = CommandOptionType.INTEGER,
            required = false
        ),
    ]
)
@Component
class TicketReplyDeadlineCommand(
    private val replyDeadlineService: ReplyDeadlineService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_REPLY_DEADLINE)) {
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

        val rawDeadline = event.getOption("until")?.asLong ?: 24

        if (rawDeadline <= 0L) {
            event.reply(translatable("ticket.command.until.not-positive"))
                .setEphemeral(true).queue()
            return
        }

        if (rawDeadline >= 8767L) {
            event.reply(translatable("ticket.command.until.to-high"))
                .setEphemeral(true).queue()
            return
        }


        val deadline = OffsetDateTime.now().plusHours(rawDeadline)
        val deadlineUnix = deadline.toEpochSecond()
        val untilString = "<t:${deadlineUnix}:F>"
        val relativeString = "<t:${deadlineUnix}:R>"

        replyDeadlineService.createDeadline(ticket, user, event.user, deadline)

        event.reply("Die Reply-Deadline wurde gesendet.").setEphemeral(true).queue()

        event.messageChannel.sendMessage(
            translatable(
                "ticket.reply-deadline.description",
                user.asMention, untilString, relativeString
            )
        ).queue()
    }
}