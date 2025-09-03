package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

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
    private val jda: JDA,
    private val messageManager: MessageManager,
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
            messageManager.getWhitelistQueryEmbed(
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
}
