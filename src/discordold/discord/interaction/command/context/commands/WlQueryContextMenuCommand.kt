package dev.slne.discordold.discord.interaction.command.context.commands

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discordold.annotation.DiscordContextMenuCommandMeta
import dev.slne.discordold.discord.interaction.command.context.DiscordContextMenuCommand
import dev.slne.discordold.discord.interaction.command.context.DiscordContextMenuCommandType
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.MessageManager
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordContextMenuCommandMeta(
    name = "Whitelist Query",
    type = DiscordContextMenuCommandType.USER,
    permission = CommandPermission.WHITELIST_QUERY,
)
class WlQueryContextMenuCommand(
    private val whitelistService: WhitelistService,
    private val messageManager: MessageManager
) : DiscordContextMenuCommand<User>() {

    override suspend fun internalExecute(
        interaction: GenericContextInteractionEvent<User>,
        hook: InteractionHook
    ) {
        hook.editOriginal(translatable("interaction.command.ticket.wlquery.querying")).await()

        val whitelists = whitelistService.findWhitelists(
            null, interaction.target.id, null
        )

        val embeds = whitelists.map { messageManager.getWhitelistQueryEmbed(it, interaction.user.name) }
        hook.editOriginal("\"" + interaction.target.asMention + "\"").setEmbeds(embeds).await()
    }
}