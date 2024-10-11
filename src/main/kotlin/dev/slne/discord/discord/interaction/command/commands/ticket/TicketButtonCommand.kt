package dev.slne.discord.discord.interaction.command.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.annotation.toJdaButton
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.processor.DiscordButtonManager
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
class TicketButtonCommand : DiscordCommand() {
    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.deleteOriginal().await()

        val channel = interaction.channel
        val openTicketInfo = DiscordButtonManager[ID]?.info ?: error("Button not found")

        sendEmbed(openTicketInfo.toJdaButton(), channel)
    }

    private suspend fun sendEmbed(button: Button, channel: MessageChannel) =
        channel.sendMessage(MessageCreate {
            embed {
                title = RawMessages.get("interaction.command.ticket.ticket-button.title")
                description =
                    RawMessages.get("interaction.command.ticket.ticket-button.description")
                color = EmbedColors.CREATE_TICKET
            }

            components += row(button)
        }).await()
}
