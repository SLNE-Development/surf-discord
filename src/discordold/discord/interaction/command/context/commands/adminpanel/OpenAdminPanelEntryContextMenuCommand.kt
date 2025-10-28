package dev.slne.discordold.discord.interaction.command.context.commands.adminpanel

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discordold.annotation.DiscordContextMenuCommandMeta
import dev.slne.discordold.discord.interaction.command.context.DiscordContextMenuCommand
import dev.slne.discordold.discord.interaction.command.context.DiscordContextMenuCommandType
import dev.slne.discordold.guild.permission.CommandPermission
import dev.slne.discordold.message.translatable
import dev.slne.discordold.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.buttons.Button

@DiscordContextMenuCommandMeta(
    name = "Show Admin Panel",
    type = DiscordContextMenuCommandType.USER,
    permission = CommandPermission.SHOW_ADMIN_PANEL,
)
class OpenAdminPanelEntryContextMenuCommand(
    private val whitelistService: WhitelistService
) : DiscordContextMenuCommand<User>() {

    override suspend fun internalExecute(
        interaction: GenericContextInteractionEvent<User>,
        hook: InteractionHook
    ) {
        val whitelists = whitelistService.findWhitelists(
            null, interaction.target.id, null
        )

        if (whitelists.isEmpty()) {
            hook.editOriginal(translatable("interaction.context.menu.admin-panel.no-whitelist-entry"))
                .await()
            return
        }

        val button = Button.link(
            "https://admin.slne.dev/core/user/${whitelists.first().uuid}",
            translatable("interaction.context.menu.admin-panel.button")
        )

        hook.editOriginal(
            translatable(
                "interaction.context.menu.admin-panel.private-message",
                interaction.target.asMention
            )
        ).setActionRow(button).await()
    }
}