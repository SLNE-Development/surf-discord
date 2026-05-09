package dev.slne.surf.discord.ticket.command.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.slne.surf.discord.command.CommandOption
import dev.slne.surf.discord.command.CommandOptionType
import dev.slne.surf.discord.command.DiscordCommand
import dev.slne.surf.discord.command.SlashCommand
import dev.slne.surf.discord.config.botConfig
import dev.slne.surf.discord.jda
import dev.slne.surf.discord.messages.translatable
import dev.slne.surf.discord.permission.DiscordPermission
import dev.slne.surf.discord.permission.hasPermission
import dev.slne.surf.discord.ticket.database.whitelist.SocialService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.springframework.stereotype.Component

@DiscordCommand(
    name = "wl-create",
    description = "Whitelist Eintrag erstellen",
    options = [CommandOption(
        name = "discord-user",
        description = "Der Discord Nutzer, der gewhitelisted werden soll",
        type = CommandOptionType.USER,
        required = true
    ),
        CommandOption(
            name = "minecraft-name",
            description = "Der Minecraft Name, der gewhitelisted werden soll.",
            type = CommandOptionType.STRING,
            required = true
        )]
)
@Component
class CreateWhitelistCommand(
    private val socialService: SocialService
) : SlashCommand {
    override suspend fun execute(event: SlashCommandInteractionEvent) {
        if (!event.member.hasPermission(DiscordPermission.WHITELIST_CREATE)) {
            event.reply(translatable("no-permission")).setEphemeral(true).queue()
            return
        }

        val userId = event.getOption("discord-user")?.asUser?.idLong ?: return
        val minecraftName = event.getOption("minecraft-name")?.asString ?: return

        if (socialService.getWhitelist(userId) != null) {
            event.reply(translatable("whitelist.command.already-whitelisted.discord"))
                .setEphemeral(true)
                .queue()
            return
        }

        if (socialService.getWhitelist(minecraftName) != null) {
            event.reply(translatable("whitelist.command.already-whitelisted.minecraft"))
                .setEphemeral(true)
                .queue()
            return
        }

        val whitelist = socialService.whitelist(userId, minecraftName)

        val discordUser = event.jda.retrieveUserById(userId).await()

        jda.guilds.forEach {
            val role = it.getRoleById(botConfig.whitelistedRoleId) ?: return@forEach
            it.addRoleToMember(discordUser, role).queue()
        }

        event.reply(
            translatable(
                "whitelist.command.create.success",
                "<@${whitelist?.discordId}>",
                whitelist?.getMinecraftName() ?: whitelist?.minecraftUuid.toString()
            )
        )
            .setEphemeral(true)
            .queue()
    }
}