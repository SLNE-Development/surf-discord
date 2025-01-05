package dev.slne.discord.discord.interaction.command.commands.misc

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.DiscordCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.EmbedColors
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime

private const val USER_IDENTIFIER = "user"

@DiscordCommandMeta(
    name = "missing-information",
    description = "This command is used to report missing information in the bot.",
    CommandPermission.MISSING_INFORMATION,
    sendTyping = true
)
object MissingInformationCommand : DiscordCommand() {
    override val options = listOf(
        option<User>(
            USER_IDENTIFIER,
            translatable("interaction.command.ticket.missing-information.arg.user")
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val user = interaction.getOptionOrThrow<User>(USER_IDENTIFIER)

        interaction.messageChannel.sendMessage(MessageCreate {
            content = user.asMention
            embed {
                title = translatable("interaction.command.ticket.missing-information.message.title")
                color = EmbedColors.MISSING_INFORMATION
                timestamp = ZonedDateTime.now()
                description =
                    translatable("interaction.command.ticket.missing-information.message.content")

                footer {
                    name = interaction.user.effectiveName
                    iconUrl = interaction.user.effectiveAvatarUrl
                }
            }
        }).await()

        hook.deleteOriginal().await()
    }
}