package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.interactions.components.getOption
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.modal.DiscordModalManager
import dev.slne.discord.discord.interaction.modal.whitelist.ChangeWhitelistModal
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val USER_OPTION: String = "user"
private const val MINECRAFT_OPTION: String = "minecraft"
private const val TWITCH_OPTION: String = "twitch"

@DiscordCommandMeta(
    name = "wlchange",
    description = "Änder die Whitelist eines Benutzers",
    permission = CommandPermission.WHITELIST_CHANGE
)
class WhitelistChangeCommand(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    private val discordModalManager: DiscordModalManager
) : DiscordCommand() {

    override val options = listOf(
        option<User>(
            USER_OPTION,
            translatable("interaction.command.wlchange.arg.user"),
            required = false
        ),
        option<String>(
            MINECRAFT_OPTION,
            translatable("interaction.command.wlchange.arg.minecraft-name"),
            required = false
        ) { length(3..16) },
        option<String>(
            TWITCH_OPTION,
            translatable("interaction.command.wlchange.arg.twitch-name"),
            required = false
        ),
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

        val minecraftUuid = minecraft?.let { userService.getUuidByUsername(it) }

        val whitelist = whitelistService.findWhitelists(
            uuid = minecraftUuid,
            discordId = user?.id,
            twitchLink = twitch
        ).firstOrNull()

        if (whitelist == null) {
            throw CommandExceptions.WHITELIST_CHANGE_NOT_WHITELISTED.create()
        }

        val modal = ChangeWhitelistModal(
            interaction.user,
            userService,
            whitelistService,
            whitelist,
            interaction.channel
        )
        discordModalManager.registerBasic(modal)
        modal.replyModal(hook)
    }
}