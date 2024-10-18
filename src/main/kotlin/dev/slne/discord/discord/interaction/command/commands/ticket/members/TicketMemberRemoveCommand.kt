package dev.slne.discord.discord.interaction.command.commands.ticket.members

import dev.minn.jda.ktx.coroutines.await
import dev.slne.discord.annotation.DiscordCommandMeta
import dev.slne.discord.discord.interaction.command.checkMemberNotBot
import dev.slne.discord.discord.interaction.command.commands.TicketCommand
import dev.slne.discord.exception.command.CommandExceptions
import dev.slne.discord.guild.permission.CommandPermission
import dev.slne.discord.message.translatable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook


private const val USER_OPTION = "user"

@DiscordCommandMeta(
    name = "remove",
    description = "Entferne einen Nutzer aus einem Ticket.",
    permission = CommandPermission.TICKET_REMOVE_USER
)
object TicketMemberRemoveCommand : TicketCommand() {

    override val options = listOf(
        option<User>(
            USER_OPTION,
            translatable("interaction.command.ticket.member.remove.arg.member"),
        )
    )


    override suspend fun internalExecute(
        interaction: SlashCommandInteractionEvent,
        hook: InteractionHook
    ) {
        val member = interaction.getOptionOrThrow<Member>(USER_OPTION)

        hook.editOriginal(translatable("interaction.command.ticket.member.remove.checking-bot"))
            .await()
        member.checkMemberNotBot(CommandExceptions.TICKET_BOT_REMOVE)

        val thread = interaction.getThreadChannelOrThrow()

        hook.editOriginal(translatable("interaction.command.ticket.member.remove.checking-in-ticket"))
            .await()
        val ticketMembers = thread.retrieveThreadMembers().await()

        if (ticketMembers.none { it.id == member.id }) {
            throw CommandExceptions.TICKET_MEMBER_NOT_IN_TICKET.create()
        }

        val executor = interaction.user
        removeTicketMember(member, executor, thread, hook)
    }

    private suspend fun removeTicketMember(
        member: Member,
        executor: User,
        thread: ThreadChannel,
        hook: InteractionHook,
    ) {
        hook.editOriginal(translatable("interaction.command.ticket.member.remove.removing"))
            .await()

        val removedMessage = thread.sendMessage(
            translatable(
                "interaction.command.ticket.member.remove.announcement",
                member.asMention, executor.asMention
            )
        ).await()

        try {
            thread.removeThreadMember(member).await()
            hook.editOriginal(translatable("interaction.command.ticket.member.remove.removed"))
                .await()
        } catch (e: Exception) {
            removedMessage.editMessage(translatable("interaction.command.ticket.member.remove.error"))
                .await()
        }
    }
}
