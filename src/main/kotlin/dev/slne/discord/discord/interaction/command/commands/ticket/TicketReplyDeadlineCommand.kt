package dev.slne.discord.discord.interaction.command.commands.ticket

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.ZonedDateTime

@DiscordCommandMeta(
    "send-reply-deadline",
    "Sends a message to the user with the reply deadline.",
    CommandPermission.TICKET_REPLY_DEADLINE,
    sendTyping = true
)
class TicketReplyDeadlineCommand : TicketCommand() {
    override val options = listOf(
        option<User>(
            USER_IDENTIFIER,
            translatable("interaction.command.ticket.reply-deadline.arg.user")
        )
    )

    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        interaction.getTicketOrThrow()

        val target = interaction.getOptionOrThrow<User>(USER_IDENTIFIER)
        sendReplyDeadlineToChannel(interaction, target, hook)
    }
}

suspend fun sendReplyDeadlineToChannel(
    interaction: Interaction,
    target: User,
    hook: InteractionHook
) {
    val deadline = ZonedDateTime.now().plusHours(36)
    val deadlineUnix = deadline.toEpochSecond()
    val untilString = "<t:${deadlineUnix}:F>"
    val relativeString = "<t:${deadlineUnix}:R>"

    interaction.messageChannel.sendMessage(
        translatable(
            "interaction.command.ticket.reply-deadline.message",
            target.asMention,
            untilString,
            relativeString
        )
    ).await()

    hook.deleteOriginal().await()
}

private const val USER_IDENTIFIER = "user"
