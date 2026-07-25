package dev.slne.surf.discord.interaction.button.impl

import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.Emojis
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component

@Component
class WhitelistInformationButton(
    private val emojis: Emojis,
    private val socialService: SocialService
) : DiscordButton {
    override val id = "whitelist:button:information"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.whitelist.view"),
            emojis.checkMark
        )
    }

    override suspend fun onClick(event: ButtonInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val ticket = event.hook.asTicketOrNull() ?: run {
            event.reply(translatable("ticket.no-ticket")).setEphemeral(true).queue()
            return
        }

        val whitelist = socialService.getWhitelist(ticket.authorId) ?: run {
            event.reply(translatable("whitelist.embed.information.no_whitelist"))
                .setEphemeral(true)
                .queue()
            return
        }

        val minecraftName = whitelist.getMinecraftName() ?: whitelist.minecraftUuid.toString()

        event.replyEmbeds(embed {
            title = translatable("whitelist.embed.information.title")
            field {
                name = translatable("whitelist.embed.information.minecraft")
                value = minecraftName
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.discord")
                value = "<@${whitelist.discordId}>"
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.blocked")
                value = whitelist.blocked.let {
                    if (it) "Ja" else "Nein"
                }
                inline = true
            }
            color = Colors.SUCCESS
        }).setEphemeral(true).queue()
    }
}