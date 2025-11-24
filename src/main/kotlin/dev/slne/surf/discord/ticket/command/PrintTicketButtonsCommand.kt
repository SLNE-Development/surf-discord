package dev.slne.surf.discord.ticket.command

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.ButtonRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Colors
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "ticketbuttons",
    description = "Sendet die Ticket Buttons in den aktuellen Kanal.",
    options = [CommandOption(
        "messageId",
        "Die ID der Nachricht, zu der die Ticket Buttons hinzugefügt werden sollen.",
        type = CommandOptionType.STRING,
        required = false,
    )]
)
@Component
class PrintTicketButtonsCommand(
    private val buttonRegistry: ButtonRegistry
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_BUTTONS)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val messageId: Long? = event.getOption("messageId")?.asLong

        if (messageId != null) {
            val channel = event.channel
            val message = channel.retrieveMessageById(messageId).submit(true).await()

            message.editMessageEmbeds(
                embed {
                    title = translatable("ticket.command.ticketbuttons.title")
                    description = translatable("ticket.command.ticketbuttons.description")

                    color = Colors.INFO
                }
            ).queue()
            message.editMessageComponents(
                ActionRow.of(
                    buttonRegistry.get("ticket:open").button,
                    buttonRegistry.get("whitelist:create").button
                )
            ).queue {
                event.reply(translatable("ticket.command.ticketbuttons.edit.success"))
                    .setEphemeral(true)
                    .queue()
            }
            return
        } else {
            event.messageChannel.sendMessageEmbeds(
                embed {
                    title = translatable("ticket.command.ticketbuttons.title")
                    description = translatable("ticket.command.ticketbuttons.description")

                    color = Colors.INFO
                }
            ).addComponents(
                ActionRow.of(
                    buttonRegistry.get("ticket:open").button,
                    buttonRegistry.get("whitelist:create").button
                )
            ).queue {
                event.reply(translatable("ticket.command.ticketbuttons.success")).setEphemeral(true)
                    .queue()
            }
        }
    }
}