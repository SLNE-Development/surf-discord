package dev.slne.surf.discord.ticket.command.context

import dev.slne.surf.discord.contextmenu.ContextCommandType
import dev.slne.surf.discord.contextmenu.DiscordContextCommand
import dev.slne.surf.discord.contextmenu.UserContextCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@DiscordContextCommand(
    "Whitelist Ansehen",
    ContextCommandType.USER
)
@Component
class ViewWhitelistInformationContextCommand(
    private val whitelistService: WhitelistService
) : UserContextCommand {
    override suspend fun execute(event: UserContextInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val whitelist = whitelistService.getWhitelist(event.target.idLong) ?: run {
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
                name = translatable("whitelist.embed.information.created")
                value = whitelist.createdAt.format(dateTimeFormatter)
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.updated")
                value = whitelist.updatedAt.format(dateTimeFormatter)
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

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
}