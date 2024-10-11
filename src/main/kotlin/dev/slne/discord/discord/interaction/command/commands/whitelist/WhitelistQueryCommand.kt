package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getThreadChannelOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.RawMessages
import dev.slne.discord.spring.feign.dto.WhitelistDTO
import dev.slne.discord.spring.service.user.UserService
import dev.slne.discord.spring.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

private const val USER_OPTION: String = "user"
private const val MINECRAFT_OPTION: String = "minecraft"
private const val TWITCH_OPTION: String = "twitch"

@DiscordCommandMeta(
    name = "wlquery",
    description = "Zeigt Whitelist Informationen über einen Benutzer an.",
    permission = CommandPermission.WHITELIST_QUERY
)
class WhitelistQueryCommand : DiscordCommand() {

    override val options
        get() = listOf(
            OptionData(
                OptionType.USER,
                USER_OPTION,
                RawMessages.get("interaction.command.ticket.wlquery.arg.user"),
                false
            ),
            OptionData(
                OptionType.STRING,
                MINECRAFT_OPTION,
                RawMessages.get("interaction.command.ticket.wlquery.arg.minecraft-name"),
                false
            ).setRequiredLength(3, 16),
            OptionData(
                OptionType.STRING,
                TWITCH_OPTION,
                RawMessages.get("interaction.command.ticket.wlquery.arg.twitch-name"),
                false
            )
        )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val channel = interaction.getThreadChannelOrThrow()

        val user = interaction.getUser(USER_OPTION)
        val minecraft = interaction.getString(MINECRAFT_OPTION)
        val twitch = interaction.getString(TWITCH_OPTION)

        if (user == null && minecraft == null && twitch == null) {
            throw CommandExceptions.TICKET_WLQUERY_NO_USER.create()
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
    ): List<WhitelistDTO> {
        var whitelists: List<WhitelistDTO> = emptyList()

        if (user != null) {
            whitelists = WhitelistService.checkWhitelists(null, user.id, null)
        } else if (twitch != null) {
            whitelists = WhitelistService.checkWhitelists(null, null, twitch)
        } else if (minecraft != null) {
            whitelists = UserService.getUuidByUsername(minecraft)?.let {
                WhitelistService.checkWhitelists(it, null, null)
            } ?: emptyList()
        }

        return whitelists
    }
}
