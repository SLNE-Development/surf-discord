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

object WhitelistCreateButton : DiscordButton {
    override val id = "whitelist:create"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.whitelist"),
            Emojis.checkMark
        )
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        val discordId = event.user.idLong
        val shouldPlay = SocialRepository.shouldBeAbleToPlayIfMember(discordId)

        if (shouldPlay) {
            event.replyComponents(
                Container.of(
                    TextDisplay.of(translatable("whitelist.survival.shouldPlay"))
                )
            ).setEphemeral(true).queue()
        } else {
            val discordToWebExists = SocialRepository.hasWebUser(discordId)
            val linkExists = SocialRepository.findLinkByDiscordId(discordId) != null

            val discordIcon = if (discordToWebExists) Emojis.checkMark else Emojis.crossMark
            val linkIcon = if (linkExists) Emojis.checkMark else Emojis.crossMark


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
