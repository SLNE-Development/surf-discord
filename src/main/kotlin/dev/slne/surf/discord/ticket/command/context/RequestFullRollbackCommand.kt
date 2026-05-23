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
    name = "request-full-rollback",
    description = "Beantragt einen Voll-Rollback für einen Spieler.",
    options = [CommandOption(
        name = "minecraft-name",
        description = "Der Minecraft Name des Spielers, für den der Rollback beantragt wird",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "server",
        description = "Der Server, auf dem der Rollback durchgeführt werden soll",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "grund",
        description = "Der Grund für den Voll-Rollback",
        type = CommandOptionType.STRING,
        required = true
    ), CommandOption(
        name = "koordinaten",
        description = "Die Koordinaten, an denen der Rollback durchgeführt werden soll (optional)",
        type = CommandOptionType.STRING,
        required = false
    ), CommandOption(
        name = "welt",
        description = "Die Welt, in der der Rollback durchgeführt werden soll (optional)",
        type = CommandOptionType.STRING,
        required = false
    )]
)
@Component
class RequestFullRollbackCommand(
    private val playerLookupService: PlayerLookupService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_VIEW)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val minecraftName = event.getOption("minecraft-name")!!.asString
        val server = event.getOption("server")!!.asString
        val reason = event.getOption("grund")!!.asString
        val coordinates = event.getOption("koordinaten")?.asString
        val world = event.getOption("welt")?.asString

        val minecraftUuid = playerLookupService.getUuid(minecraftName) ?: run {
            event.reply(translatable("whitelist.survival.modal.user-not-found"))
                .setEphemeral(true)
                .queue()
            return
        }

        event.replyEmbeds(embed {
            author = event.user.effectiveName
            authorIconUrl = event.user.effectiveAvatarUrl

            title = translatable("rollback.command.full.title", minecraftName)
            titleUrl = "$PLAYER_PANEL_BASE_URL/$minecraftUuid"
            thumbnail = "$PLAYER_HEAD_BASE_URL/$minecraftUuid"
            color = Colors.ERROR
            timestamp = OffsetDateTime.now()

            field {
                name = translatable("rollback.command.full.field.reason")
                value = reason
                inline = false
            }
            field {
                name = translatable("whitelist.embed.information.minecraft")
                value = "```\n$minecraftName\n```"
                inline = false
            }
            field {
                name = translatable("rollback.command.full.field.uuid")
                value = "```\n$minecraftUuid\n```"
                inline = false
            }
            field {
                name = translatable("rollback.command.full.field.server")
                value = server
                inline = false
            }
            world?.let {
                field {
                    name = translatable("rollback.command.full.field.world")
                    value = it
                    inline = false
                }
            }
            coordinates?.let {
                field {
                    name = translatable("rollback.command.full.field.coordinates")
                    value = "```\n$it\n```"
                    inline = false
                }
            }
        }).queue()
    }
}
