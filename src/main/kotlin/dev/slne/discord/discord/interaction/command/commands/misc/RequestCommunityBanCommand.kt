package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getThreadChannelOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime
import java.util.*


private const val MINECRAFT_OPTION: String = "minecraft-name"
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

    override val options = listOf(
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

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val minecraftName = interaction.getOption<String>(MINECRAFT_OPTION)
            ?: throw CommandExceptions.TICKET_WLQUERY_NO_USER.create()

        hook.editOriginal(translatable("interaction.command.ticket.wlquery.querying")).await()
        val minecraftUuid = userService.getUuidByUsername(minecraftName)
            ?: throw CommandExceptions.MINECRAFT_USER_NOT_FOUND.create()

        val whitelists = whitelistService.findWhitelists(minecraftUuid, null, null)


        hook.deleteOriginal().await()

        val channel = interaction.getThreadChannelOrThrow()

        val reason = interaction.getOption<String>(REASON_OPTION)
            ?: throw CommandExceptions.GENERIC.create()

        val banLink = interaction.getOption<String>(BAN_LINK_OPTION)
            ?: throw CommandExceptions.GENERIC.create()


        if (whitelists.isEmpty()) {

            val embed = buildEmbed(
                minecraftName,
                minecraftUuid,
                false,
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
            true,
            firstWhitelist.discordId,
            firstWhitelist.twitchLink,
            reason,
            banLink,
            interaction.user
        )
        channel.sendMessageEmbeds(whitelistEmbed).await()
    }

    private fun buildEmbed(
        minecraftName: String,
        minecraftUuid: UUID,
        isWhitelist: Boolean,
        discordUserId: String?,
        twitchLink: String?,
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

            if (isWhitelist) {
                field {
                    name = "Discord User"
                    value = if (discordUserId == null) "Kein Benutzer" else "<@$discordUserId>"
                    inline = false
                }

                field {
                    name = "Twitch Link"
                    value = "https://twitch/$twitchLink"
                    inline = false
                }
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