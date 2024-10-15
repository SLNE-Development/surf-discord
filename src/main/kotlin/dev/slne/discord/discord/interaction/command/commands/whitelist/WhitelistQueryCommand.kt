package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.interactions.components.getOption
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getThreadChannelOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.RawMessages
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
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
object WhitelistQueryCommand : DiscordCommand() {

    override val options = listOf(
        option<User>(
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.user"),
            required = false
        ),
        option<String>(
            MINECRAFT_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.minecraft-name"),
            required = false
        ) { length(3..16) },
        option<String>(
            TWITCH_OPTION,
            RawMessages.get("interaction.command.ticket.wlquery.arg.twitch-name"),
            required = false
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val channel = interaction.getThreadChannelOrThrow()

        val user = interaction.getOption<User>(USER_OPTION)
        val minecraft = interaction.getOption<String>(MINECRAFT_OPTION)
        val twitch = interaction.getOption<String>(TWITCH_OPTION)

        if (user == null && minecraft == null && twitch == null) {
            throw CommandExceptions.TICKET_WLQUERY_NO_USER()
        }

        val whitelists = getWhitelists(user, minecraft, twitch)

        if (user != null) {
            MessageManager.printUserWlQuery(whitelists, user.name, channel, hook)
        } else if (minecraft != null) {
            MessageManager.printUserWlQuery(whitelists, minecraft, channel, hook)
        } else if (twitch != null) {
            MessageManager.printUserWlQuery(whitelists, twitch, channel, hook)
        }
    }

    private suspend fun getWhitelists(
        user: User?,
        minecraft: String?,
        twitch: String?
    ) = if (user != null) {
        WhitelistService.checkWhitelists(null, user.id, null)
    } else if (twitch != null) {
        WhitelistService.checkWhitelists(null, null, twitch)
    } else if (minecraft != null) {
        UserService.getUuidByUsername(minecraft)?.let {
            WhitelistService.checkWhitelists(it, null, null)
        } ?: emptyList()
    } else {
        throw CommandExceptions.TICKET_WLQUERY_NO_USER()
    }
}
