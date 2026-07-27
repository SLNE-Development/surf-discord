package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.awt.Color

object OpenTicketButton : DiscordButton {
    override val id = "ticket:open"
    override val button by lazy { Button.success(id, translatable("button.ticket.open")) }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val menu = SelectMenuRegistry.get("ticket:type").create(event.hook)

        event.replyEmbeds(embed {
            title = translatable("ticket.open.title")
            description = translatable("ticket.open.description")
            color = Color(31, 189, 210)
        }).addComponents(ActionRow.of(menu)).setEphemeral(true).queue()
    }
}