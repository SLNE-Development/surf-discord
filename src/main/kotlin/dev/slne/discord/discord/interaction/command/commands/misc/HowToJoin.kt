package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.components.getOption
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook

private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "howtojoin",
    description = "Erklärt, wie man dem CastCrafter-Server beitreten kann, inklusive Infos zur Whitelist.",
    permission = CommandPermission.HOW_TO_JOIN,
    deferReply = false
)
object HowToJoin : DiscordCommand() {
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

        interaction.reply(MessageCreate {
            if (user != null) {
                content = user.asMention
            }

            embed {
                title = translatable("command.howtojoin.message.title")
                description = translatable("command.howtojoin.message.content")
                color = EmbedColors.HOW_TO_JOIN
            }
        }).await()
    }
}