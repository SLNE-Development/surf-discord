package dev.slne.surf.discord.ticket.command.whitelist

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@DiscordCommand(
    name = "wl-view",
    description = "Whitelist Eintrag ansehen",
    options = [CommandOption(
        name = "user",
        description = "Der Discord Nutzer, dessen Whitelist Eintrag gezeigt werden soll",
        type = CommandOptionType.USER,
        required = true
    )]
)
@Component
class ViewWhitelistCommand(
    private val whitelistService: WhitelistService
) : SlashCommand {
    private val modalRegistry by lazy {
        getBean<ModalRegistry>()
    }

    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val userId = event.getOption("user")?.asUser?.idLong ?: return

        val whitelist = whitelistService.getWhitelist(userId) ?: run {
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
                value = event.jda.getUserById(whitelist.discordId)?.asMention
                    ?: whitelist.discordId.toString()
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