package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

@DiscordCommandMeta(
    name = "whitelist-unblock",
    description = "Entblockt einen User von der Whitelist",
    permission = CommandPermission.WHITELIST_UNBLOCK
)
class WhitelistUnblockCommand(private val whitelistService: WhitelistService) : DiscordCommand() {

    override val options = listOf(
        option<User>("user", translatable("interaction.command.whitelist-unblock.option.user"))
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getOptionOrThrow<User>("user")
        val whitelists = whitelistService.findWhitelists(discordId = user.id)

        if (whitelists.isEmpty()) {
            throw CommandExceptions.WHITELIST_QUERY_NO_ENTRIES.create(user.asMention)
        }

        whitelists.forEach {
            it.blocked = false

            whitelistService.saveWhitelist(it)
        }

        hook.sendMessage(
            translatable("interaction.command.whitelist-unblock.success", user.name)
        ).await()
    }
}
