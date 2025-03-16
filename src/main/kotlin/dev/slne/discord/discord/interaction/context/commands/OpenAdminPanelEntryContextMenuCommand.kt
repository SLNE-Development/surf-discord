package dev.slne.discord.discord.interaction.context.commands

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordContextMenuCommandMeta
import dev.slne.discord.discord.interaction.context.DiscordContextMenuCommand
import dev.slne.discord.discord.interaction.context.DiscordContextMenuCommandType
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.whitelist.WhitelistService
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

        val privateChannel = interaction.user.openPrivateChannel().await()

        try {
            if (whitelists.isEmpty()) {
                privateChannel.sendMessage(translatable("interaction.context.menu.admin-panel.no-whitelist-entry"))
                    .await()

                return
            }

            val button = Button.link(
                "https://admin.slne.dev/core/user/${whitelists.first().uuid}",
                translatable("interaction.context.menu.admin-panel.button")
            )

            privateChannel
                .sendMessage(
                    translatable(
                        "interaction.context.menu.admin-panel.private-message",
                        interaction.target.asMention
                    )
                )
                .addActionRow(button)
                .await()


            hook.deleteOriginal().await()
        } catch (e: Exception) {
            hook.editOriginal(translatable("interaction.context.menu.admin-panel.no-private-message-allowed"))
                .await()
        }
    }
}