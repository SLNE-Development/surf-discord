package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.messages.InlineEmbed
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime
import java.util.*

private const val TIMED_SUBCOMMAND: String = "timed"
private const val FULL_SUBCOMMAND: String = "full"

private const val MINECRAFT_OPTION: String = "minecraft-name"
private const val SERVERS_OPTION: String = "servers"

private const val TIMERANGE_OPTION: String = "timerange"
private const val REASON_OPTION: String = "reason"

private const val DIMENSION_OPTION: String = "dimension"
private const val COORDINATES_OPTION: String = "coordinates"

@DiscordCommandMeta(
    name = "requestrollback",
    description = "Fordere einen Rollback für einen User an",
    permission = CommandPermission.REQUEST_ROLLBACK,
    ephemeral = true
)
class RequestRollbackCommand(
    private val whitelistService: WhitelistService,
    private val userService: UserService
) : DiscordCommand() {

    override val subCommands = listOf(
        subcommand(
            TIMED_SUBCOMMAND,
            translatable("interaction.command.rollback.sub.timed.description")
        ) {
            this@subcommand.addOptions(
                listOf(
                    option<String>(
                        MINECRAFT_OPTION,
                        translatable("interaction.command.rollback.arg.player_name"),
                        required = true
                    ),
                    option<String>(
                        SERVERS_OPTION,
                        translatable("interaction.command.rollback.arg.servers"),
                        required = true
                    ),
                    option<String>(
                        TIMERANGE_OPTION,
                        translatable("interaction.command.rollback.sub.timed.arg.timerange"),
                        required = true
                    ),
                    option<String>(
                        DIMENSION_OPTION,
                        translatable("interaction.command.rollback.sub.arg.dimension"),
                        required = true
                    ),
                    option<String>(
                        COORDINATES_OPTION,
                        translatable("interaction.command.rollback.sub.arg.coordinates"),
                        required = true
                    )
                )
            )
        },
        subcommand(
            FULL_SUBCOMMAND,
            translatable("interaction.command.rollback.sub.full.description")
        ) {
            this@subcommand.addOptions(
                listOf(
                    option<String>(
                        MINECRAFT_OPTION,
                        translatable("interaction.command.rollback.arg.player_name"),
                        required = true
                    ),
                    option<String>(
                        SERVERS_OPTION,
                        translatable("interaction.command.rollback.arg.servers"),
                        required = true
                    ),
                    option<String>(
                        REASON_OPTION,
                        translatable("interaction.command.rollback.sub.full.arg.reason"),
                        required = true
                    ),
                    option<String>(
                        DIMENSION_OPTION,
                        translatable("interaction.command.rollback.sub.arg.dimension"),
                        required = true
                    ),
                    option<String>(
                        COORDINATES_OPTION,
                        translatable("interaction.command.rollback.sub.arg.coordinates"),
                        required = true
                    )
                )
            )
        },
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val minecraftName = interaction.getOption<String>(MINECRAFT_OPTION)
        val servers = interaction.getOption<String>(SERVERS_OPTION)
        val dimension = interaction.getOption<String>(DIMENSION_OPTION)
        val coordinates = interaction.getOption<String>(COORDINATES_OPTION)

        val subCommand = interaction.subcommandName

        if (minecraftName == null) {
            throw CommandExceptions.TICKET_WLQUERY_NO_USER.create()
        }

        hook.editOriginal(translatable("interaction.command.ticket.wlquery.querying")).await()
        val whitelists = userService.getUuidByUsername(minecraftName)?.let {
            whitelistService.findWhitelists(it, null, null)
        } ?: emptyList()

        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(minecraftName)
        }

        val firstWhitelist = whitelists.first()

        if (dimension == null) {
            throw CommandExceptions.GENERIC.create()
        }

        if (coordinates == null) {
            throw CommandExceptions.GENERIC.create()
        }

        if (servers == null) {
            throw CommandExceptions.GENERIC.create()
        }

        when (subCommand) {
            TIMED_SUBCOMMAND -> {
                val timerange = interaction.getOption<String>(TIMERANGE_OPTION)
                    ?: throw CommandExceptions.GENERIC.create()

                hook.deleteOriginal().await()

                val channel = hook.interaction.channel as TextChannel
                val embed = buildEmbed(
                    minecraftName,
                    firstWhitelist.uuid,
                    firstWhitelist.discordId,
                    servers,
                    interaction.user,
                    fullRollback = false,
                    dimension,
                    coordinates
                ) {
                    it.field {
                        name = "Timerange"
                        value = timerange
                        inline = false
                    }
                }

                channel.sendMessageEmbeds(embed).await()
            }

            FULL_SUBCOMMAND -> {
                val reason = interaction.getOption<String>(REASON_OPTION)
                    ?: throw CommandExceptions.GENERIC.create()

                hook.deleteOriginal().await()

                val channel = hook.interaction.channel as TextChannel
                val embed = buildEmbed(
                    minecraftName,
                    firstWhitelist.uuid,
                    firstWhitelist.discordId,
                    servers,
                    interaction.user,
                    fullRollback = true,
                    dimension,
                    coordinates
                ) {
                    it.field {
                        name = "Grund"
                        value = reason
                        inline = false
                    }
                }

                channel.sendMessageEmbeds(embed).await()
            }
        }
    }

    private fun buildEmbed(
        minecraftName: String,
        minecraftUuid: UUID,
        discordUserId: String?,
        servers: String,
        requestedBy: User,
        fullRollback: Boolean,
        dimension: String,
        coordinates: String,
        builder: (InlineEmbed) -> Unit
    ): MessageEmbed {
        return Embed {
            title = "${if (fullRollback) "Voll" else "Teil"}-Rollback: $minecraftName"
            url = "https://admin.slne.dev/core/user/$minecraftUuid"
            thumbnail = "https://minotar.net/helm/${minecraftUuid}/128.png"
            timestamp = ZonedDateTime.now()

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
                name = "Servers"
                value = servers
                inline = false
            }

            field {
                name = "Dimension"
                value = dimension
                inline = false
            }

            field {
                name = "Koordinaten"
                value = coordinates
                inline = false
            }

            builder(this)
        }
    }
}