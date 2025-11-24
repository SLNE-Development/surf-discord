package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Emojis
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class CloseTicketButton(
    private val selectMenuRegistry: SelectMenuRegistry,
    private val emojis: Emojis
) : DiscordButton {
    override val id = "ticket:close"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.close"),
            emojis.crossMark
        )
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_CLOSE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val selectMenu = selectMenuRegistry.get("ticket:close:reason").create(event.hook)

        event.reply(translatable("ticket.close.selectreason")).setEphemeral(true).addComponents(
            ActionRow.of(selectMenu)
        ).queue()
    }
}