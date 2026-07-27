package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.whitelist.SocialRepository
import dev.slne.surf.discord.util.Emojis
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.components.container.Container
import net.dv8tion.jda.api.components.textdisplay.TextDisplay
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistCreateButton(
    private val emojis: Emojis,
    private val socialRepository: SocialRepository
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

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val discordId = event.user.idLong
        val shouldPlay = socialRepository.shouldBeAbleToPlayIfMember(discordId)

        if (shouldPlay) {
            event.replyComponents(
                Container.of(
                    TextDisplay.of(translatable("whitelist.survival.shouldPlay"))
                )
            ).setEphemeral(true).queue()
        } else {
            val discordToWebExists = socialRepository.hasWebUser(discordId)
            val linkExists = socialRepository.findLinkByDiscordId(discordId) != null

            val discordIcon = if (discordToWebExists) emojis.checkMark else emojis.crossMark
            val linkIcon = if (linkExists) emojis.checkMark else emojis.crossMark


            event.replyComponents(
                Container.of(
                    TextDisplay.of(
                        translatable(
                            "whitelist.survival.missingLink",
                            discordIcon.name,
                            linkIcon.name
                        )
                    )
                )
            ).setEphemeral(true).queue()
        }
    }
}
