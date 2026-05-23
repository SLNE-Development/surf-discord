package dev.slne.surf.discord.ticket.command.context

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.dsl.embed
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.util.Colors
import dev.slne.surf.discord.util.PlayerLookupService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

private const val PLAYER_PANEL_BASE_URL = "https://support.castcrafter.de/core/surf-players"
private const val PLAYER_HEAD_BASE_URL = "https://mc-heads.net/avatar"

@DiscordCommand(
    name = "request-refund",
    description = "Beantragt eine Erstattung für einen Spieler.",
    options = [CommandOption(
        name = "grund",
        description = "Der Grund für die Erstattung",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "ticket",
        description = "Das Ticket, zu dem die Erstattung gehört",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "erstattung",
        description = "Was erstattet werden soll.",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "server",
        description = "Der Server, auf dem die Erstattung durchgeführt werden soll",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "welt",
        description = "Die Welt, in der die Erstattung durchgeführt werden soll",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "koordinaten",
        description = "Die Koordinaten, an denen die Erstattung durchgeführt werden soll",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "minecraft-name",
        description = "Der Minecraft Name des Spielers, an den die Erstattung gerichtet ist (optional)",
        type = CommandOptionType.STRING,
        required = false
    )]
)
@Component
class RequestRefundCommand(
    private val playerLookupService: PlayerLookupService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val minecraftName = event.getOption("minecraft-name")?.asString
        val reason = event.getOption("grund")!!.asString
        val ticket = event.getOption("ticket")!!.asString
        val item = event.getOption("erstattung")!!.asString
        val server = event.getOption("server")!!.asString
        val world = event.getOption("welt")!!.asString
        val coordinates = event.getOption("koordinaten")!!.asString

        val minecraftUuid = if (minecraftName != null) {
            playerLookupService.getUuid(minecraftName) ?: run {
                event.reply(translatable("whitelist.survival.modal.user-not-found"))
                    .setEphemeral(true)
                    .queue()
                return
            }
        } else null

        event.replyEmbeds(embed {
            author = event.user.effectiveName
            authorIconUrl = event.user.effectiveAvatarUrl

            title = translatable("refund.command.title", minecraftName ?: translatable("refund.command.no-player"))
            minecraftUuid?.let { titleUrl = "$PLAYER_PANEL_BASE_URL/$it" }
            minecraftUuid?.let { thumbnail = "$PLAYER_HEAD_BASE_URL/$it" }
            color = Colors.INFO
            timestamp = OffsetDateTime.now()

            field {
                name = translatable("refund.command.field.reason")
                value = reason
                inline = false
            }
            field {
                name = translatable("refund.command.field.ticket")
                value = ticket
                inline = false
            }
            minecraftName?.let {
                field {
                    name = translatable("whitelist.embed.information.minecraft")
                    value = "```\n$it\n```"
                    inline = false
                }
            }
            minecraftUuid?.let {
                field {
                    name = translatable("refund.command.field.uuid")
                    value = "```\n$it\n```"
                    inline = false
                }
            }
            field {
                name = translatable("refund.command.field.item")
                value = item
                inline = false
            }
            field {
                name = translatable("refund.command.field.server")
                value = server
                inline = false
            }
            field {
                name = translatable("refund.command.field.world")
                value = world
                inline = false
            }
            field {
                name = translatable("refund.command.field.coordinates")
                value = "```\n$coordinates\n```"
                inline = false
            }
        }).queue()
    }
}