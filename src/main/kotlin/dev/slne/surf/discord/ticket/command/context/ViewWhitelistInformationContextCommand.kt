package dev.slne.surf.discord.ticket.command.context

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.discord.contextmenu.ContextCommandType
import dev.slne.surf.discord.contextmenu.DiscordContextCommand
import dev.slne.surf.discord.contextmenu.UserContextCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialRepository
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent

@DiscordContextCommand(
    "Whitelist Ansehen",
    ContextCommandType.USER
)
object ViewWhitelistInformationContextCommand : UserContextCommand {
    override suspend fun execute(event: UserContextInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val link = SocialRepository.findLinkByDiscordId(event.target.idLong) ?: run {
            event.reply(translatable("whitelist.embed.information.no_link"))
                .setEphemeral(true)
                .queue()
            return
        }

        val minecraftName =
            PlayerLookupService.getUsername(link.minecraftUuid) ?: link.minecraftUuid.toString()
        val isDiscordMember = runCatching {
            event.guild?.retrieveMemberById(link.discordId)?.await()
        }.getOrNull() != null

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