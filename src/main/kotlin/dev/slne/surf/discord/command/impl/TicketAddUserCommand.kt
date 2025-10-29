package dev.slne.surf.discord.command.impl

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import java.awt.Color

@DiscordCommand(
    "add", "Füge einen Nutzer zum Ticket hinzu", options = [
        CommandOption("user", "Der hinzuzufügende Nutzer", CommandOptionType.USER, true)
    ]
)
@Component
class TicketAddUserCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("user")?.asUser ?: error("User option is missing")
        val ticket = event.hook.asTicketOrNull()

        if (ticket == null) {
            event.reply("Du musst dich in einem Ticket befinden, um diesen Befehl zu nutzen.")
                .setEphemeral(true).queue()
            return
        }

        val thread = ticket.getThreadChannel()
            ?: error("Failed to get ticket thread of ticket ${ticket.ticketId}")

        thread.addThreadMember(user).queue()

        event.reply("${user.asMention} wurde zum Ticket hinzugefügt.").setEphemeral(true).queue()
        thread.sendMessage(user.asMention).queue()
        thread.sendMessageEmbeds(embed {
            title = "Willkommen im Ticket"
            description =
                "Du wurdest zu diesem Ticket hinzugefügt. Bitte sieh dir den Verlauf des Tickets an und warte auf eine Nachricht eines Teammitglieds."
            color = Color.YELLOW
            footer = "Hinzugefügt von ${event.user.name}"
        }).queue()
    }
}