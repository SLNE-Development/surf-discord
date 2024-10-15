package dev.slne.discord.discord.interaction.command.commands.whitelist

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.discord.interaction.command.getThreadChannelOrThrow
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.getDiscordGuildByGuildId
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.MessageManager
import dev.slne.discord.message.RawMessages
import dev.slne.discord.persistence.feign.dto.WhitelistDTO
import dev.slne.discord.persistence.service.user.UserService
import dev.slne.discord.persistence.service.whitelist.WhitelistService
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val USER_OPTION: String = "user"
private const val MINECRAFT_OPTION: String = "minecraft"
private const val TWITCH_OPTION: String = "twitch"

@DiscordCommandMeta(
    name = "whitelist",
    description = "Füge einen Spieler zur Whitelist hinzu.",
    permission = CommandPermission.WHITELIST
)
object WhitelistCommand : DiscordCommand() {

    override val options = listOf(
        option<User>(
            USER_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.user")
        ),
        option<String>(
            MINECRAFT_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.minecraft-name")
        ) { length(3..16) },
        option<String>(
            TWITCH_OPTION,
            RawMessages.get("interaction.command.ticket.whitelist.arg.twitch-name")
        )
    )


    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val channel = interaction.getThreadChannelOrThrow()
        val user = interaction.getOptionOrThrow<User>(USER_OPTION)
        val minecraft = interaction.getOptionOrThrow<String>(
            MINECRAFT_OPTION,
            exceptionMessage = "Du musst einen Minecraft Namen angeben."
        )
        val twitch = interaction.getOptionOrThrow<String>(
            TWITCH_OPTION,
            exceptionMessage = "Du musst einen Twitch Namen angeben."
        )
        val discordId = user.id
        val executor = interaction.user

        whitelistUser(interaction, hook, user, executor, minecraft, twitch, discordId, channel)
    }

    private suspend fun whitelistUser(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook,
        user: User,
        executor: User?,
        minecraft: String,
        twitch: String,
        discordId: String,
        channel: ThreadChannel
    ) {
        val minecraftUuid = UserService.getUuidByUsername(minecraft)
            ?: throw CommandExceptions.MINECRAFT_USER_NOT_FOUND.create()

        hook.editOriginal(RawMessages.get("interaction.command.ticket.whitelist.checking")).await()

        val whitelists: List<WhitelistDTO> = WhitelistService.checkWhitelists(
            minecraftUuid, discordId, twitch
        )

        if (whitelists.isNotEmpty()) {
            hook.editOriginal(
                RawMessages.get("interaction.command.ticket.whitelist.already-whitelisted")
            ).await()

            for (whitelist: WhitelistDTO in whitelists) {
                channel.sendMessageEmbeds(MessageManager.getWhitelistQueryEmbed(whitelist)).await()
            }
        } else {
            hook.editOriginal(
                RawMessages.get("interaction.command.ticket.whitelist.adding")
            ).await()

            val createdWhitelist: WhitelistDTO? = WhitelistService.addWhitelist(
                WhitelistDTO.createFrom(
                    minecraftUuid,
                    minecraft,
                    twitch,
                    user,
                    executor
                )
            )

            if (createdWhitelist == null) {
                throw CommandExceptions.TICKET_WHITELIST()
            }

            addWhitelistedRole(interaction.guild, user)

            channel.sendMessage(MessageCreate {
                content = RawMessages.get(
                    "interaction.command.ticket.whitelist.added",
                    user.asMention
                )
                embeds += MessageManager.getWhitelistQueryEmbed(createdWhitelist)
            }).await()

            hook.deleteOriginal().await()
        }
    }

    private suspend fun addWhitelistedRole(guild: Guild?, user: User) {
        if (guild == null) return
        val discordGuild = getDiscordGuildByGuildId(guild.id)?.discordGuild ?: return
        val roleId = discordGuild.whitelistRoleId
        val role = guild.getRoleById(roleId) ?: return

        guild.addRoleToMember(user, role).await()
    }
}
