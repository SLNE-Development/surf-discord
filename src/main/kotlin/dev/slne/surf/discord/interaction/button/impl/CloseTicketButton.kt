package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component

@Component
class CloseTicketButton(
    private val selectMenuRegistry: SelectMenuRegistry
) : DiscordButton {
    override val id = "ticket:close"
    override val button =
        Button.of(ButtonStyle.SECONDARY, id, "Ticket schließen", Emoji.fromFormatted("❌"))

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val selectMenu = selectMenuRegistry.get("ticket:close:reason").create(event.hook)

        event.deferReply(true).queue {
            it.editOriginal("Wähle den Grund...").setActionRow(selectMenu).queue()
        }
    }
}