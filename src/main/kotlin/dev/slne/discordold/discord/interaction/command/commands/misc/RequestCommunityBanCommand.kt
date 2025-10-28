package dev.slne.discordold.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.discord.interaction.command.DiscordCommand
import dev.slne.discordold.discord.interaction.command.getThreadChannelOrThrow
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.user.UserService
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime
import java.util.*

private const val QUERY_SUBCOMMAND: String = "query"
private const val MANUAL_SUBCOMMAND: String = "manual"

private const val MINECRAFT_OPTION: String = "minecraft-name"
private const val DISCORD_OPTION: String = "discord-id"
private const val TWITCH_OPTION: String = "twitch-name"

private const val REASON_OPTION: String = "reason"
private const val BAN_LINK_OPTION: String = "ban-link"

@DiscordCommandMeta(
    name = "request-community-ban",
    description = "Fordere einen Community-Ban für einen User an",
    permission = CommandPermission.REQUEST_COMMUNITY_BAN,
    ephemeral = true
)
class RequestCommunityBanCommand(
    private val whitelistService: WhitelistService,
    private val userService: UserService
) : DiscordCommand() {


    override val subCommands = listOf(
        subcommand(
            QUERY_SUBCOMMAND,
            translatable("interaction.command.community-ban.normal.description")
        ) {
            this@subcommand.addOptions(
                listOf(
                    option<String>(
                        MINECRAFT_OPTION,
                        translatable("interaction.command.community-ban.arg.player_name"),
                        required = true
                    ),
                    option<String>(
                        BAN_LINK_OPTION,
                        translatable("interaction.command.community-ban.arg.ban-link"),
                        required = true
                    ),
                    option<String>(
                        REASON_OPTION,
                        translatable("interaction.command.community-ban.full.arg.reason"),
                        required = true
                    ),
                )
            )
        }, subcommand(
            MANUAL_SUBCOMMAND,
            translatable("interaction.command.community-ban.custom.description")
        ) {
            this@subcommand.addOptions(
                listOf(
                    option<String>(
                        MINECRAFT_OPTION,
                        translatable("interaction.command.community-ban.arg.player_name"),
                        required = true
                    ),
                    option<String>(
                        BAN_LINK_OPTION,
                        translatable("interaction.command.community-ban.arg.ban-link"),
                        required = true
                    ),
                    option<String>(
                        REASON_OPTION,
                        translatable("interaction.command.community-ban.full.arg.reason"),
                        required = true
                    ),
                    option<String>(
                        DISCORD_OPTION,
                        translatable("interaction.command.community-ban.arg.discord_name"),
                        required = false
                    ),
                    option<String>(
                        TWITCH_OPTION,
                        translatable("interaction.command.community-ban.arg.twitch_name"),
                        required = false
                    )
                )
            )
        }
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val minecraftName = interaction.getOption<String>(MINECRAFT_OPTION)

        val minecraftUuid = userService.getUuidByUsername(minecraftName!!)
            ?: throw CommandExceptions.MINECRAFT_USER_NOT_FOUND.create()

        val subCommand = interaction.subcommandName

        val channel = interaction.getThreadChannelOrThrow()

        val reason = interaction.getOption<String>(REASON_OPTION)
            ?: throw CommandExceptions.GENERIC.create()

        val banLink = interaction.getOption<String>(BAN_LINK_OPTION)
            ?: throw CommandExceptions.GENERIC.create()

        when (subCommand) {
            QUERY_SUBCOMMAND -> {
                hook.editOriginal(translatable("interaction.command.ticket.wlquery.querying"))
                    .await()

                val whitelists = whitelistService.findWhitelists(minecraftUuid, null, null)

                hook.deleteOriginal().await()
                if (whitelists.isEmpty()) {

                    val embed = buildEmbed(
                        minecraftName,
                        minecraftUuid,
                        null,
                        null,
                        reason,
                        banLink,
                        interaction.user
                    )

                    channel.sendMessageEmbeds(embed).await()
                    return@internalExecute
                }

                val firstWhitelist = whitelists.first()

                val whitelistEmbed = buildEmbed(
                    minecraftName,
                    minecraftUuid,
                    firstWhitelist.discordId,
                    firstWhitelist.twitchLink,
                    reason,
                    banLink,
                    interaction.user
                )

                channel.sendMessageEmbeds(whitelistEmbed).await()
            }

            MANUAL_SUBCOMMAND -> {

                val twitchName = interaction.getOption<String>(TWITCH_OPTION)
                val discordUserId = interaction.getOption<String>(DISCORD_OPTION)

                hook.deleteOriginal().await()
                val embed = buildEmbed(
                    minecraftName,
                    minecraftUuid,
                    discordUserId,
                    twitchName,
                    reason,
                    banLink,
                    interaction.user
                )

                channel.sendMessageEmbeds(embed).await()
            }
        }
    }

    private fun buildEmbed(
        minecraftName: String,
        minecraftUuid: UUID?,
        discordUserId: String?,
        twitchName: String?,
        reason: String,
        banLink: String,
        requestedBy: User,
    ): MessageEmbed {
        return Embed {
            title = "Community-Ban: $minecraftName"
            url = "https://admin.slne.dev/core/user/$minecraftUuid"
            thumbnail = "https://minotar.net/helm/${minecraftUuid}/128.png"
            timestamp = ZonedDateTime.now()
            color = 0xFF0000

            author {
                name = requestedBy.effectiveName
                iconUrl = requestedBy.effectiveAvatarUrl
            }

            field {
                name = "Minecraft Name"
                value = minecraftName
                inline = false
            }

            field {
                name = "Minecraft UUID"
                value = minecraftUuid.toString()
                inline = false
            }

            field {
                name = "Discord User"
                value = if (discordUserId == null) "Kein Benutzer" else "<@$discordUserId>"
                inline = false
            }

            field {
                name = "Twitch Link"
                value = if (twitchName == null) "Kein Benutzer" else "https://twitch/$twitchName"
                inline = false
            }

            field {
                name = "Reason"
                value = reason
                inline = false
            }

            field {
                name = "Ban Link"
                value = banLink
                inline = false
            }
        }
    }
}
