package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.row
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.annotation.toJdaButton
import dev.slne.discord.discord.interaction.button.DiscordButtonProcessor
import dev.slne.discord.discord.interaction.button.buttons.NotifyEventRoleTicketButtonId
import dev.slne.discord.discord.interaction.button.buttons.NotifySurvivalRoleTicketButtonId
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.buttons.Button

private const val ID = "ticket-buttons"

@DiscordCommandMeta(
    name = ID,
    description = "Print the ticket button to add a notify-role and embed.",
    permission = CommandPermission.NOTIFY_BUTTONS
)

class NotifyRoleButtonCommand(private val buttonProcessor: DiscordButtonProcessor) : DiscordCommand() {
    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        hook.deleteOriginal().await()

        val channel = interaction.channel
        val notifyRoleSurvival =
            buttonProcessor[NotifySurvivalRoleTicketButtonId]?.first ?: error("Button not found")
        val notifyRoleEvent =
            buttonProcessor[NotifyEventRoleTicketButtonId]?.first ?: error("Button not found")

        sendEmbed(notifyRoleSurvival.toJdaButton(), notifyRoleEvent.toJdaButton(), channel)
    }

    private suspend fun sendEmbed(button1: Button, button2 : Button, channel: MessageChannel) =
        channel.sendMessage(MessageCreate {
            embed {
                title = translatable("interaction.button.notifybutton.title")
                description =
                    translatable("interaction.button.notifybutton.description")
                color = EmbedColors.CREATE_TICKET
            }

            components += row(button1, button2)
        }).await()
}
