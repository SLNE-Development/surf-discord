package dev.slne.discord.discord.interaction.button.buttons

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.DiscordBot
import dev.slne.discord.annotation.DiscordButton
import dev.slne.discord.annotation.DiscordEmoji
import dev.slne.discord.discord.interaction.button.DiscordButtonHandler
import dev.slne.discord.discord.interaction.select.menus.TicketsMenu
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.RawMessages.Companion.get
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu

@DiscordButton(
    id = "open-ticket",
    label = "Ticket öffnen",
    style = ButtonStyle.SUCCESS,
    emoji = DiscordEmoji(unicode = "🎫")
)
class OpenTicketButton : DiscordButtonHandler {
    override suspend fun ButtonInteraction.onClick() {
        TicketsMenu(id).apply {
            DiscordBot.selectMenuManager.addMenu(this)

            sendEmbed(build(), this@onClick)
        }
    }

    private suspend fun sendEmbed(menu: SelectMenu, interaction: ButtonInteraction) {
        val embed = Embed {
            title = get("interaction.button.open-ticket.title")
            description = get("interaction.button.open-ticket.description")
            color = EmbedColors.SELECT_TICKET_TYPE
        }

        interaction.deferReply(true).await().sendMessageEmbeds(embed).setActionRow(menu)
            .setEphemeral(true).await()
    }
}
