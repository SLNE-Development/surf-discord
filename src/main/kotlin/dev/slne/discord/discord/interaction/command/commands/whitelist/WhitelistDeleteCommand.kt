package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
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
    private val userService: UserService
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
        hook.editOriginal(translatable("interaction.command.whitelist.delete.delete.querying"))
            .await()

        val isWhitelisted = whitelistService.isWhitelisted(
            minecraftUuid
        )

        if (!isWhitelisted) {
            throw CommandExceptions.WHITELIST_DELETE_NOT_WHITELISTED.create()
        }

        hook.editOriginal(translatable("interaction.command.whitelist.delete.querying")).await()

        whitelistService.deleteWhitelists(minecraftUuid)

        hook.editOriginal(
            translatable(
                "interaction.command.whitelist.delete.success",
                minecraftUsername
            )
        ).await()
    }
}