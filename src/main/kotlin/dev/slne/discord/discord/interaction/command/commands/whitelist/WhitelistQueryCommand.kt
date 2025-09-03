package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.Embed
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.jda
import dev.slne.discord.message.EmbedColors.WL_QUERY
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.external.Whitelist
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime

private const val USER_OPTION: String = "user"
private const val MINECRAFT_OPTION: String = "minecraft"
private const val TWITCH_OPTION: String = "twitch"

@DiscordCommandMeta(
    name = "wlquery",
    description = "Zeigt Whitelist Informationen über einen Benutzer an.",
    permission = CommandPermission.WHITELIST_QUERY
)
class WhitelistQueryCommand(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    private val messageManager: MessageManager
) : DiscordCommand() {

    override val options = listOf(
        option<User>(
            USER_OPTION,
            translatable("interaction.command.ticket.wlquery.arg.user"),
            required = false
        ),
        option<String>(
            MINECRAFT_OPTION,
            translatable("interaction.command.ticket.wlquery.arg.minecraft-name"),
            required = false
        ) { length(3..16) },
        option<String>(
            TWITCH_OPTION,
            translatable("interaction.command.ticket.wlquery.arg.twitch-name"),
            required = false
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getOption<User>(USER_OPTION)
        val minecraft = interaction.getOption<String>(MINECRAFT_OPTION)
        val twitch = interaction.getOption<String>(TWITCH_OPTION)

        if (user == null && minecraft == null && twitch == null) {
            throw CommandExceptions.TICKET_WLQUERY_NO_USER.create()
        }

        val name = user?.name ?: minecraft ?: twitch ?: "Unknown User"
        val whitelists = getWhitelists(user, minecraft, twitch)

        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(name)
        }

        hook.editOriginal(translatable("interaction.command.ticket.wlquery.querying")).await()
        hook.editOriginalEmbeds(whitelists.map {
            getEmbed(
                it,
                interaction.user.name
            )
        }).await()
    }

    private suspend fun getWhitelists(
        user: User?,
        minecraft: String?,
        twitch: String?
    ) = if (user != null) {
        whitelistService.findWhitelists(null, user.id, null)
    } else if (twitch != null) {
        whitelistService.findWhitelists(null, null, twitch)
    } else if (minecraft != null) {
        userService.getUuidByUsername(minecraft)?.let {
            whitelistService.findWhitelists(it, null, null)
        } ?: emptyList()
    } else {
        throw CommandExceptions.TICKET_WLQUERY_NO_USER.create()
    }

    private suspend fun getEmbed(whitelist: Whitelist, requester: String?) = Embed {
        title = translatable("whitelist.query.embed.title")
        footer {
            name = if (requester != null) {
                translatable("whitelist.query.embed.footer", requester)
            } else {
                translatable("whitelist.query.embed.footer.no-requester")
            }
            iconUrl = jda.selfUser.avatarUrl
        }
        description = null
        color = WL_QUERY
        timestamp = ZonedDateTime.now()

        val minecraftName = userService.getUsernameByUuid(whitelist.uuid)
        val twitchLink = whitelist.twitchLink
        val uuid = whitelist.uuid
        val discordUser = whitelist.user?.await()
        val addedBy = whitelist.addedBy?.await()

        field {
            name = translatable("whitelist.query.embed.field.uuid")
            value = uuid.toString()
            inline = false
        }

        if (minecraftName != null) {
            field {
                name = translatable("whitelist.query.embed.field.minecraft-name")
                value = "`${minecraftName}`"
            }
        }

        field {
            name = translatable("whitelist.query.embed.field.twitch-name")
            value = "[${twitchLink}](${whitelist.clickableTwitchLink})"
        }

        if (discordUser != null) {
            field {
                name = translatable("whitelist.query.embed.field.discord-user")
                value = discordUser.asMention
            }
        }

        if (addedBy != null) {
            field {
                name = translatable("whitelist.query.embed.field.added-by")
                value = addedBy.asMention
            }
        }

        field {
            name = translatable("whitelist.query.embed.field.blocked")
            value = if (whitelist.blocked) translatable("common.yes") else translatable("common.no")
        }
    }
}
