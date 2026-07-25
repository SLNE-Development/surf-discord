package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import dev.slne.surf.discord.util.Emojis
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistCreateButton(
    private val emojis: Emojis,
    private val socialService: SocialService
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

        val link = socialService.findLink(discordId) ?: run {
            event.reply(translatable("whitelist.survival.not-linked"))
                .setEphemeral(true)
                .queue()
            return
        }

        if (socialService.isWhitelisted(discordId)) {
            event.reply(translatable("whitelist.survival.already_whitelisted"))
                .setEphemeral(true)
                .queue()
            return
        }

        socialService.whitelist(link)

        event.member?.let { member ->
            event.guild?.addRoleToMember(
                member,
                event.guild?.getRoleById(botConfig.whitelistedRoleId)
                    ?: error("Whitelisted role not found")
            )?.queue()
        }

        event.reply(translatable("whitelist.survival.success"))
            .setEphemeral(true)
            .queue()
    }
}
