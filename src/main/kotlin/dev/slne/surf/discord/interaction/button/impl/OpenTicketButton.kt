package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.selectmenu.SelectMenuRegistry
import dev.slne.surf.discord.messages.translatable
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class OpenTicketButton(
    private val selectMenuRegistry: SelectMenuRegistry
) : DiscordButton {
    override val id = "ticket:open"
    override val button by lazy { Button.success(id, translatable("button.ticket.open")) }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val menu = selectMenuRegistry.get("ticket:type").create(event.hook)

        event.deferReply(true).queue {
            it.sendMessageEmbeds(
                embed {
                    title = translatable("ticket.open.title")
                    description = translatable("ticket.open.description")
                    color = Color(31, 189, 210)
                }
            ).addActionRow(menu).setEphemeral(true).queue()
        }
    }
}