package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import org.springframework.stereotype.Component

@Component
class WhitelistProceedButton : DiscordButton {
    override val id = "ticket:whitelist:complete"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.whitelist.proceed"),
            Emoji.fromCustom("checkmark", 1433072075446423754, false)
        )
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.TICKET_WHITELIST_CONFIRM)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val modal = getBean<ModalRegistry>().get("ticket:whitelist:proceed").create(event.hook)
        event.replyModal(modal).queue()
    }
}