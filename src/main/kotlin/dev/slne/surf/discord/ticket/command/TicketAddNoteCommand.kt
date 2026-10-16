package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.notes.TicketNotesRepository
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@DiscordCommand(
    name = "add-note",
    description = "Add a note to a ticket",
    options = [
        CommandOption(
            name = "content",
            description = "The content of the note",
            type = CommandOptionType.STRING,
            required = true
        ),
    ]
)
object TicketAddNoteCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_ADD)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val content =
            event.getOption("content")?.asString ?: "Invalid Content (NO_CONTENT_PROVIDED)"
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply(translatable("ticket.command.not-a-ticket"))
                .setEphemeral(true).queue()
            return
        }

        val member = event.member ?: run {
            event.reply(translatable("ticket.command.add-note.no-member"))
                .setEphemeral(true).queue()
            return
        }

        TicketNotesRepository.addNote(ticket, member, content)

        event.replyComponents(
            Container.of(
                listOf(
                    TextDisplay.of(
                        "**Ticket Notiz hinzugefügt**"
                    ),
                    TextDisplay.of(
                        "<@${member.id}> hat eine interne Notiz zu diesem Ticket hinzugefügt."
                    )
                )
            ).withAccentColor(Colors.INFO)
        ).useComponentsV2().queue()
    }

    private val thumbnail =
        Thumbnail.fromUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSizVdqJ6GdjpehVzRYkJt_XiihFI7li0DEDfgCwT3mVy0Xywbl3LYZAa8&s=10")
}