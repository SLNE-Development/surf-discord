package dev.slne.discordold.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discordold.annotation.DiscordCommandMeta
import dev.slne.discordold.discord.interaction.command.DiscordCommand
import dev.slne.discordold.exception.command.CommandExceptions
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.MessageManager
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.user.UserService
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val MINECRAFT_USER_OPTION: String = "minecraft"

@DiscordCommandMeta(
    name = "wldelete",
    description = "Löscht die Whitelist des angegebenen Spielers",
    permission = CommandPermission.WHITELIST_DELETE
)
class WhitelistDeleteCommand(
    private val whitelistService: WhitelistService,
    private val userService: UserService,
    private val messageManager: MessageManager
) : DiscordCommand() {

    override val options = listOf(
        option<String>(
            MINECRAFT_USER_OPTION,
            translatable("interaction.command.whitelist.delete.arg.user"),
            required = true
        ) { length(3..16) }
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val minecraftUsername = interaction.getOptionOrThrow<String>(MINECRAFT_USER_OPTION)

        hook.editOriginal(translatable("interaction.command.whitelist.delete.uuid-fetching"))
            .await()

        val minecraftUuid = userService.getUuidByUsername(minecraftUsername)
        hook.editOriginal(translatable("interaction.command.whitelist.delete.querying"))
            .await()

        val isWhitelisted = whitelistService.isWhitelisted(
            minecraftUuid
        )

        if (!isWhitelisted) {
            throw CommandExceptions.WHITELIST_DELETE_NOT_WHITELISTED.create()
        }

        hook.editOriginal(translatable("interaction.command.whitelist.delete.delete")).await()

        whitelistService.deleteWhitelists(minecraftUuid)

        hook.editOriginal(
            translatable(
                "interaction.command.whitelist.delete.success",
                minecraftUsername
            )
        ).await()

        hook.sendMessage(messageManager.buildWhitelistDeletedEmbed(interaction.user, minecraftUsername, minecraftUuid.toString())).await()
    }
}