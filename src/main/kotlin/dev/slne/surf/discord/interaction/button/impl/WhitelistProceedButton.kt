package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component

@Component
class WhitelistProceedButton : DiscordButton {
    override val id = "ticket:whitelist:complete"
    override val button = Button.of(
        ButtonStyle.SECONDARY,
        id,
        "Whitelist annehmen",
        Emoji.fromCustom("checkmark", 1433072075446423754, false)
    )

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val modal = getBean<ModalRegistry>().get("ticket:whitelist:proceed").create(event.hook)
        event.replyModal(modal).queue()
    }
}