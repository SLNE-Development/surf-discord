package dev.slne.surf.discord.ticket.command.whitelist

import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.getBean
import dev.slne.surf.discord.interaction.modal.ModalRegistry
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.WhitelistService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "wl-edit",
    description = "Whitelist Eintrag bearbeiten",
    options = [CommandOption(
        name = "user",
        description = "Der Discord Nutzer, dessen Whitelist Eintrag bearbeitet werden soll",
        type = CommandOptionType.USER,
        required = true
    )]
)
@Component
class EditWhitelistCommand(
    private val whitelistService: WhitelistService
) : SlashCommand {
    private val modalRegistry by lazy {
        getBean<ModalRegistry>()
    }

    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_EDIT)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val userId = event.getOption("user")?.asUser?.idLong ?: return

        val whitelist = whitelistService.getWhitelist(userId) ?: run {
            event.reply(translatable("whitelist.embed.information.no_whitelist"))
                .setEphemeral(true)
                .queue()
            return
        }

        val minecraftName = whitelist.getMinecraftName() ?: whitelist.minecraftUuid.toString()
        val discordName =
            event.jda.getUserById(whitelist.discordId)?.name ?: whitelist.discordId.toString()

        event.replyModal(
            modalRegistry.get("whitelist:modal:edit-survival")
                .create(
                    event.hook,
                    discordName,
                    minecraftName,
                    whitelist.discordId.toString(),
                    if (whitelist.blocked) "Ja" else "Nein",
                    if (whitelist.blocked) "true" else "false"
                )
        ).queue()
    }
}