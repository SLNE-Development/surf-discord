package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@Component
@DiscordCommand("ticketbuttons", "Sendet die Ticket Buttons in den aktuellen Kanal.")
class PrintTicketButtonsCommand(private val buttonRegistry: ButtonRegistry) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_BUTTONS)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        event.messageChannel.sendMessageEmbeds(
            embed {
                title = translatable("ticket.command.ticketbuttons.title")
                description = translatable("ticket.command.ticketbuttons.description")

                color = Colors.INFO
                footer = translatable("ticket.command.ticketbuttons.footer")
            }
        ).addComponents(
            ActionRow.of(buttonRegistry.get("ticket:open").button)
        ).queue {
            event.reply(translatable("ticket.command.ticketbuttons.success")).setEphemeral(true)
                .queue()
        }
    }
}