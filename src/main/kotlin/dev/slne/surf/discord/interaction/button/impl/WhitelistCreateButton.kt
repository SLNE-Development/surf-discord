package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import dev.slne.surf.discord.util.Emojis
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistCreateButton(
    private val emojis: Emojis,
    private val whitelistService: WhitelistService
) : DiscordButton {
    override val id = "whitelist:create"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.whitelist"),
            emojis.checkMark
        )
    }

    private val modalRegistry by lazy {
        getBean<ModalRegistry>()
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
//        event.replyEmbeds(embed {
//            title = "Fehlgeschlagen"
//            description =
//                "Zurzeit können keine Whitelist Anträge eingereicht werden. Der Survival Server ist zurzeit in Wartungsarbeiten und somit eine Whitelist nicht möglich. Bitte habe Verständnis."
//            color = Colors.WARNING
//        }).setEphemeral(true).queue()
        // return

        if (whitelistService.isWhitelisted(event.user.idLong)) {
            event.reply(translatable("whitelist.survival.modal.already_whitelisted"))
                .setEphemeral(true)
                .queue()
            return
        }

        event.replyModal(modalRegistry.get("whitelist:modal:create-survival").create()).queue()
    }
}