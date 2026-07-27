package dev.slne.surf.discord.interaction.button.impl

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.interaction.button.DiscordButton
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialRepository
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.Emojis
import dev.slne.surf.discord.util.PlayerLookupService
import dev.slne.surf.discord.util.asTicketOrNull
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object WhitelistInformationButton : DiscordButton {
    override val id = "whitelist:button:information"
    override val button by lazy {
        Button.of(
            ButtonStyle.SECONDARY,
            id,
            translatable("button.ticket.whitelist.view"),
            Emojis.checkMark
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

        val link = SocialRepository.findLinkByDiscordId(ticket.authorId) ?: run {
            event.reply(translatable("whitelist.embed.information.no_link"))
                .setEphemeral(true)
                .queue()
            return
        }

        val minecraftName =
            PlayerLookupService.getUsername(link.minecraftUuid) ?: link.minecraftUuid.toString()
        val isDiscordMember = event.guild?.retrieveMemberById(link.discordId)?.await() != null

        event.replyEmbeds(embed {
            title = translatable("whitelist.embed.information.title")
            field {
                name = translatable("whitelist.embed.information.minecraft")
                value = minecraftName
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.discord")
                value = "<@${link.discordId}>"
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.blocked")
                value = isDiscordMember.let {
                    if (it) "Nein" else "Ja"
                }
                inline = true
            }
            color = Colors.SUCCESS
        }).setEphemeral(true).queue()
    }
}