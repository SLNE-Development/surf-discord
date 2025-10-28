package dev.slne.discordold.discord.interaction.button.buttons

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discordold.annotation.DiscordButton
import dev.slne.discordold.annotation.DiscordEmoji
import dev.slne.discordold.discord.interaction.button.DiscordButtonHandler
import dev.slne.discordold.discord.interaction.modal.DiscordModalManager
import dev.slne.discordold.discord.interaction.select.DiscordSelectMenuManager
import dev.slne.discordold.discord.interaction.select.menus.TicketsMenu
import dev.slne.discordold.message.EmbedColors
import dev.slne.discordold.message.translatable
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu

const val OpenTicketButtonId = "open-ticket"

@DiscordButton(
    OpenTicketButtonId,
    "Ticket öffnen",
    ButtonStyle.SUCCESS,
    DiscordEmoji(unicode = "🎫")
)
class OpenTicketButton(
    private val discordSelectMenuManager: DiscordSelectMenuManager,
    private val discordModalManager: DiscordModalManager
) : DiscordButtonHandler {

    override suspend fun ButtonInteractionEvent.onClick() {
        val menu = TicketsMenu(id, discordModalManager)
        discordSelectMenuManager.addMenu(menu)

        sendEmbed(menu.build(), interaction)
    }

    private suspend fun sendEmbed(menu: SelectMenu, interaction: ButtonInteraction) {
        val embed = Embed {
            title = translatable("interaction.button.open-ticket.title")
            description = translatable("interaction.button.open-ticket.description")
            color = EmbedColors.SELECT_TICKET_TYPE
        }

        interaction.deferReply(true)
            .await()
            .sendMessageEmbeds(embed)
            .setActionRow(menu)
            .setEphemeral(true)
            .await()
    }

}
