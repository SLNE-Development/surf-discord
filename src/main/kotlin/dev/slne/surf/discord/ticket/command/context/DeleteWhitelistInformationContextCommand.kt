package dev.slne.surf.discord.ticket.command.context

import dev.slne.surf.discord.contextmenu.ContextCommandType
import dev.slne.surf.discord.contextmenu.DiscordContextCommand
import dev.slne.surf.discord.contextmenu.UserContextCommand
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import org.springframework.stereotype.Component

@DiscordContextCommand(
    "Whitelist löschen",
    ContextCommandType.USER
)
@Component
class DeleteWhitelistInformationContextCommand(
    private val socialService: SocialService
) : UserContextCommand {
    private val modalRegistry by lazy {
        getBean<ModalRegistry>()
    }

    override suspend fun execute(event: UserContextInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_DELETE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val whitelist = socialService.getWhitelist(event.target.idLong) ?: run {
            event.reply(translatable("whitelist.embed.information.no_whitelist"))
                .setEphemeral(true)
                .queue()
            return
        }

        val minecraftName = whitelist.getMinecraftName() ?: whitelist.minecraftUuid.toString()
        val discordName =
            event.jda.getUserById(whitelist.discordId)?.name ?: whitelist.discordId.toString()

        event.replyModal(
            modalRegistry.get("whitelist:modal:delete-survival")
                .create(
                    event.hook,
                    discordName,
                    minecraftName,
                    whitelist.discordId.toString()
                )
        ).queue()
    }
}