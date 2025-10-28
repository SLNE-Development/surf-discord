package dev.slne.discordold.discord.interaction.command.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.annotation.toJdaButton
import dev.slne.discordold.discord.interaction.button.DiscordButtonProcessor
import dev.slne.discordold.discord.interaction.button.buttons.OpenTicketButtonId
import dev.slne.discordold.discord.interaction.command.DiscordCommand
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.EmbedColors
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.buttons.Button

private const val ID = "ticket-buttons"

@DiscordCommandMeta(
    name = ID,
    description = "Print the ticket button and embed.",
    permission = CommandPermission.TICKET_BUTTONS
)
class TicketButtonCommand(private val buttonProcessor: DiscordButtonProcessor) : DiscordCommand() {
    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.deleteOriginal().await()

        val channel = interaction.channel
        val openTicketInfo =
            buttonProcessor[OpenTicketButtonId]?.first ?: error("Button not found")

        sendEmbed(openTicketInfo.toJdaButton(), channel)
    }

    private suspend fun sendEmbed(button: Button, channel: MessageChannel) =
        channel.sendMessage(MessageCreate {
            embed {
                title = translatable("interaction.command.ticket.ticket-button.title")
                description =
                    translatable("interaction.command.ticket.ticket-button.description")
                color = EmbedColors.CREATE_TICKET
            }

            components += row(button)
        }).await()
}
