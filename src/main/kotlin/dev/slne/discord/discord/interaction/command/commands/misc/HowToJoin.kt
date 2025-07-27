package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.MessageEdit
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import dev.slne.discord.cooldown.CooldownManager
import dev.slne.discord.cooldown.CooldownDuration
import dev.slne.discord.cooldown.CooldownKey
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "howtojoin",
    description = "Erklärt, wie man dem CastCrafter-Server beitreten kann, inklusive Infos zur Whitelist.",
    permission = CommandPermission.HOW_TO_JOIN,
    ephemeral = false
)
class HowToJoin : DiscordCommand() {
    override val options = listOf(
        option<User>(
            USER_OPTION,
            translatable("command.howtojoin.arg.user.description"),
            required = false
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getOption<User>(USER_OPTION)
        val cooldownManager = CooldownManager()
        val commandName = interaction.name
        val channelId = interaction.channel.id.toLong()

        val key = CooldownKey(channelId, commandName)

        if (cooldownManager.isOnCooldown(channelId, commandName)) {
            val remainingSeconds = cooldownManager.getRemainingMillis(key) / 1000
            val message = translatable("interaction.command.cooldown.active", remainingSeconds.toString())
            hook.editOriginal(message).await()
            return
        }

        hook.editOriginal(MessageEdit {
            if (user != null) {
                content = user.asMention
            }

            embed {
                title = translatable("command.howtojoin.message.title")
                description = translatable("command.howtojoin.message.content")
                color = EmbedColors.HOW_TO_JOIN
            }
        }).await()

        cooldownManager.setCooldown(channelId, CooldownDuration.HOW_TO_JOIN)
    }
}