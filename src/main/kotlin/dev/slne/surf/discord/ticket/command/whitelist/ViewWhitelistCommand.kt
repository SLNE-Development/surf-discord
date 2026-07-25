package dev.slne.surf.discord.ticket.command.whitelist

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialEntry
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import dev.slne.surf.discord.util.Colors
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "wl-view",
    description = "Whitelist Eintrag ansehen",
    options = [CommandOption(
        name = "discord-user",
        description = "Der Discord Nutzer, dessen Whitelist Eintrag gezeigt werden soll",
        type = CommandOptionType.USER,
        required = false
    ), CommandOption(
        name = "minecraft-name",
        description = "Der Minecraft Name, dessen Whitelist Eintrag gezeigt werden soll",
        type = CommandOptionType.STRING,
        required = false
    )]
)
@Component
class ViewWhitelistCommand(
    private val socialService: SocialService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val userId = event.getOption("discord-user")?.asUser?.idLong
        val minecraftNameOption = event.getOption("minecraft-name")?.asString

        val whitelist = when {
            userId != null -> socialService.getWhitelist(userId)
            minecraftNameOption != null -> socialService.getWhitelist(minecraftNameOption)
            else -> {
                event.reply(translatable("whitelist.command.view.missing-parameters"))
                    .setEphemeral(true)
                    .queue()
                return
            }
        }

        if (whitelist == null) {
            event.reply(translatable("whitelist.embed.information.no_whitelist"))
                .setEphemeral(true)
                .queue()
            return
        }

        event.replyEmbeds(whitelist.toInformationEmbed()).setEphemeral(true).queue()
    }

    private suspend fun SocialEntry.toInformationEmbed(): MessageEmbed {
        val minecraftName = getMinecraftName() ?: minecraftUuid.toString()

        return embed {
            title = translatable("whitelist.embed.information.title")
            field {
                name = translatable("whitelist.embed.information.minecraft")
                value = minecraftName
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.discord")
                value = "<@$discordId>"
                inline = true
            }
            field {
                name = translatable("whitelist.embed.information.blocked")
                value = if (blocked) "Ja" else "Nein"
                inline = true
            }
            color = Colors.SUCCESS
        }
    }
}
