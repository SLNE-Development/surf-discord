package dev.slne.surf.discord.ticket.command

import dev.minn.jda.ktx.coroutines.await
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
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.section.Section
import net.dv8tion.jda.api.components.separator.Separator
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.components.thumbnail.Thumbnail
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@DiscordCommand(
    name = "ticketbuttons",
    description = "Sendet die Ticket Buttons in den aktuellen Kanal.",
    options = [CommandOption(
        "message",
        "Die ID der Nachricht, zu der die Ticket Buttons hinzugefügt werden sollen.",
        type = CommandOptionType.STRING,
        required = false,
    )]
)
object PrintTicketButtonsCommand : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.COMMAND_TICKET_BUTTONS)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val messageId: Long? = event.getOption("message")?.asLong

        if (messageId != null) {
            val channel = event.channel
            val message = channel.retrieveMessageById(messageId).submit(true).await()

            if (message.author.idLong != event.jda.selfUser.idLong) {
                event.reply(translatable("ticket.command.ticketbuttons.edit.not-bot-message"))
                    .setEphemeral(true)
                    .queue()
                return
            }

            message.editMessageEmbeds().await()

            message.editMessageComponents(
                Container.of(
                    Section.of(
                        Thumbnail.fromUrl("https://castcrafter.de/favicon.png"),
                        TextDisplay.of(translatable("ticket.command.ticketbuttons.title")),
                        TextDisplay.of(translatable("ticket.command.ticketbuttons.description"))
                    ),
                    Separator.createDivider(Separator.Spacing.LARGE),
                    TextDisplay.of(translatable("ticket.command.ticketbuttons.whitelist")),
                    Separator.createDivider(Separator.Spacing.LARGE),
                    ActionRow.of(
                        ButtonRegistry.get("ticket:open").button,
                        ButtonRegistry.get("whitelist:create").button
                    )
                )
            ).useComponentsV2().queue {
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
                    ButtonRegistry.get("ticket:open").button,
                    ButtonRegistry.get("whitelist:create").button
                )
            ).queue {
                event.reply(translatable("ticket.command.ticketbuttons.success")).setEphemeral(true)
                    .queue()
            }
        }
    }
}